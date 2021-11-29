package me.Mohamad82.RUoM.database.mysql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.Mohamad82.RUoM.Ruom;
import me.Mohamad82.RUoM.database.Database;
import me.Mohamad82.RUoM.database.Query;
import me.Mohamad82.RUoM.database.enums.Priority;
import me.Mohamad82.RUoM.utils.ServerVersion;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MySQLDatabase extends Database {

    private final static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(Ruom.getPlugin().getName().toLowerCase() + "-mysql-thread-%d").build();
    private final ExecutorService threadPool;

    private final MySQLCredentials credentials;
    private final int poolingSize;
    private int poolingUsed = 0;
    private HikariDataSource hikari;

    public MySQLDatabase(MySQLCredentials credentials, int poolingSize) {
        this.credentials = credentials;
        this.poolingSize = poolingSize;

        threadPool = Executors.newFixedThreadPool(poolingSize, threadFactory);
    }

    @Override
    public void connect() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(credentials.getUrl());
        hikariConfig.setDriverClassName(ServerVersion.supports(13) ? "com.mysql.cj.jdbc.Driver" : "com.mysql.jdbc.Driver");
        hikariConfig.setUsername(credentials.getUsername());
        hikariConfig.setPassword(credentials.getPassword());
        hikariConfig.setMaximumPoolSize(poolingSize);

        this.hikari = new HikariDataSource(hikariConfig);
        queueTask = startQueue();
    }

    @Override
    public void shutdown() {
        queueTask.cancel();
        queue.clear();
        hikari.close();
    }

    @Override
    public BukkitTask startQueue() {
        return new BukkitRunnable() {
            public void run() {
                List<Priority> priorities = new ArrayList<>(Arrays.asList(Priority.values()));

                if (poolingUsed >= poolingSize) {
                    tickQueue(this);
                    return;
                }

                for (Priority priority : priorities) {
                    List<Query> queries = queue.get(priority);
                    if (queries.isEmpty()) continue;

                    Set<Query> removedQueries = new HashSet<>();
                    for (Query query : queries) {
                        if (query.getStatusCode() == Query.StatusCode.FINISHED.getCode())
                            removedQueries.add(query);
                    }
                    queries.removeAll(removedQueries);

                    for (Query query : queries) {
                        if (query.hasDoneRequirements() && query.getStatusCode() != Query.StatusCode.RUNNING.getCode()) {
                            query.setStatusCode(Query.StatusCode.RUNNING.getCode());

                            executeQuery(query).whenComplete((statusCode, error) -> Ruom.runSync(() -> {
                                query.setStatusCode(statusCode);
                                poolingUsed--;
                            }, 1));

                            poolingUsed++;
                            if (poolingUsed >= poolingSize) break;
                        }
                    }
                    if (poolingUsed >= poolingSize) break;
                    if (!queries.isEmpty()) break;
                }

                tickQueue(this);
            }
        }.runTask(Ruom.getPlugin());
    }

    private CompletableFuture<Integer> executeQuery(Query query) {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Connection connection = createConnection();
                try {
                    PreparedStatement preparedStatement = query.createPreparedStatement(connection);
                    ResultSet resultSet = null;

                    if (query.getStatement().startsWith("INSERT") ||
                            query.getStatement().startsWith("UPDATE") ||
                            query.getStatement().startsWith("DELETE") ||
                            query.getStatement().startsWith("CREATE"))
                        preparedStatement.executeUpdate();
                    else
                        resultSet = preparedStatement.executeQuery();

                    query.getCompletableFuture().complete(resultSet);

                    closeConnection(connection);
                    completableFuture.complete(Query.StatusCode.FINISHED.getCode());
                } catch (SQLException e) {
                    Ruom.error("Failed to perform a query in the sqlite database. Stacktrace:");
                    Ruom.debug("Statement: " + query.getStatement());
                    e.printStackTrace();

                    query.increaseFailedAttempts();
                    if (query.getFailedAttempts() > failAttemptRemoval) {
                        closeConnection(connection);
                        completableFuture.complete(Query.StatusCode.FINISHED.getCode());
                        Ruom.warn("This query has been removed from the queue as it exceeded the maximum failures." +
                                " It's more likely to see some stuff break because of this failure, Please report" +
                                " this bug to the developers.\n" +
                                "Developer(s) of this plugin: " + Ruom.getPlugin().getDescription().getAuthors());
                    }

                    closeConnection(connection);
                    completableFuture.complete(Query.StatusCode.FAILED.getCode());
                }
            }
        };

        threadPool.submit(runnable);

        return completableFuture;
    }

    private Connection createConnection() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            Ruom.error("Failed to establish mysql connection!");
            e.printStackTrace();
            return null;
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            Ruom.error("Failed to close a mysql connection!");
            e.printStackTrace();
        }
    }

}
