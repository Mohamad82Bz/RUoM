package me.mohamad82.ruom.database.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.mohamad82.ruom.database.Database;
import me.mohamad82.ruom.database.Priority;
import me.mohamad82.ruom.database.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class MySQLExecutor extends Database {

    protected final ExecutorService threadPool;
    private final MySQLCredentials credentials;

    protected HikariDataSource hikari;
    protected final int poolingSize;
    protected int poolingUsed = 0;

    public MySQLExecutor(MySQLCredentials credentials, int poolingSize, ThreadFactory threadFactory) {
        this.credentials = credentials;
        this.poolingSize = poolingSize;

        threadPool = Executors.newFixedThreadPool(Math.max(1, poolingSize), threadFactory);
    }

    protected void connect(String driverClassName) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(credentials.getUrl());
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setUsername(credentials.getUsername());
        hikariConfig.setPassword(credentials.getPassword());
        hikariConfig.setMaximumPoolSize(poolingSize);
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");

        this.hikari = new HikariDataSource(hikariConfig);
    }

    protected void tick() {
        List<Priority> priorities = new ArrayList<>(Arrays.asList(Priority.values()));

        for (Priority priority : priorities) {
            List<Query> queries = new ArrayList<>(queue.get(priority));
            if (queries.isEmpty()) continue;

            Set<Query> removedQueries = new HashSet<>();
            for (Query query : queries) {
                if (query.getStatusCode() == Query.StatusCode.FINISHED.getCode())
                    removedQueries.add(query);
            }
            queries.removeAll(removedQueries);
            queue.get(priority).removeAll(removedQueries);

            for (Query query : queries) {
                if (query.hasDoneRequirements() && query.getStatusCode() != Query.StatusCode.RUNNING.getCode()) {
                    query.setStatusCode(Query.StatusCode.RUNNING.getCode());

                    executeQuery(query).whenComplete((statusCode, error) -> {
                        query.setStatusCode(statusCode);
                        poolingUsed--;
                    });

                    poolingUsed++;
                    if (poolingUsed >= poolingSize) break;
                }
            }
            if (poolingUsed >= poolingSize) break;
            if (!queries.isEmpty()) break;
        }
    }

    @Override
    public void runQuery(Query query) {
        executeQuerySync(query);
    }

    public Query.StatusCode executeQuerySync(Query query) {
        Connection connection = createConnection();
        try {
            PreparedStatement preparedStatement = query.createPreparedStatement(connection);
            ResultSet resultSet = null;

            if (query.getStatement().startsWith("INSERT") ||
                    query.getStatement().startsWith("UPDATE") ||
                    query.getStatement().startsWith("DELETE") ||
                    query.getStatement().startsWith("CREATE")||
                    query.getStatement().startsWith("ALTER"))
                preparedStatement.executeUpdate();
            else
                resultSet = preparedStatement.executeQuery();

            query.complete(resultSet);

            closeConnection(connection);

            return Query.StatusCode.FINISHED;
        } catch (SQLException e) {
            onQueryFail(query);
            e.printStackTrace();

            query.increaseFailedAttempts();
            if (query.getFailedAttempts() > failAttemptRemoval) {
                closeConnection(connection);
                onQueryRemoveDueToFail(query);

                return Query.StatusCode.FINISHED;
            }

            closeConnection(connection);

            return Query.StatusCode.FAILED;
        }
    }

    protected CompletableFuture<Integer> executeQuery(Query query) {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                completableFuture.complete(executeQuerySync(query).getCode());
            }
        };

        threadPool.submit(runnable);

        return completableFuture;
    }

    private Connection createConnection() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onQueryFail(Query query);

    protected abstract void onQueryRemoveDueToFail(Query query);

}
