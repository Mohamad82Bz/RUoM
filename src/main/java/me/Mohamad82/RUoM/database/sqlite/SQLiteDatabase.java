package me.Mohamad82.RUoM.database.sqlite;

import me.Mohamad82.RUoM.RUoMPlugin;
import me.Mohamad82.RUoM.Ruom;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SQLiteDatabase {

    private final Map<Priority, List<SQLiteQuery>> queue = new HashMap<>();

    private final File dbFile;
    private Connection connection;
    private BukkitTask queueTask;
    private int failAttemptRemoval = 2;

    public SQLiteDatabase(File dbFile) {
        this.dbFile = dbFile;
        try {
            if (!dbFile.exists())
                dbFile.createNewFile();
        } catch (IOException e) {
            Ruom.error("Failed to create the sqlite database file. Stacktrace:");
            e.printStackTrace();
        }

        for (Priority priority : Priority.values()) {
            queue.put(priority, new ArrayList<>());
        }
    }

    /**
     * Initializes the sqlite connection.
     * @param afterConnectStatements Statements that will be ran after the connection.
     */
    public void connect(String... afterConnectStatements) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());

        startQueue();

        for (String statement : afterConnectStatements) {
            queueQuery(statement, Priority.HIGHEST);
        }
    }

    /**
     * Shutdowns the sqlite database.
     */
    public void shutdown() {
        try {
            queue.clear();
            connection.close();
            Ruom.log("SQLite database has been shutdown.");
        } catch (SQLException e) {
            Ruom.error("Failed to shutdown sqlite database. Stacktrace:");
            e.printStackTrace();
        }
    }

    /**
     * Queues a query.
     * @param statement Statement that is going to run.
     * @param priority Priority of the query in queue. Higher priorities will be ran sooner in the queue.
     * @return SQLiteQuery class that contains CompletableFuture with ResultSet callback. Useful when you need the ResultSet of the query.
     * @see SQLiteQuery
     */
    public SQLiteQuery queueQuery(String statement, Priority priority) {
        CompletableFuture<ResultSet> completableFuture = new CompletableFuture<>();
        SQLiteQuery sqLiteQuery = new SQLiteQuery(completableFuture, statement);
        queue.get(priority).add(sqLiteQuery);

        return sqLiteQuery;
    }

    /**
     * Queues a query with normal priority.
     * @param statement Statement that is going to run.
     * @return SQLiteQuery class that contains CompletableFuture with ResultSet callback. Useful when you need the ResultSet of the query.
     * @see SQLiteQuery
     */
    public SQLiteQuery queueQuery(String statement) {
        CompletableFuture<ResultSet> completableFuture = new CompletableFuture<>();
        SQLiteQuery sqLiteQuery = new SQLiteQuery(completableFuture, statement);
        queue.get(Priority.NORMAL).add(sqLiteQuery);

        return sqLiteQuery;
    }

    /**
     * Creates a table in the sqlite database. Priority will be the highest.
     * Argument example:
     * UUID VARCHAR(100),Name VARCHAR(100),PRIMARY KEY (UUID)
     * @param tableName The table name that is going to be created.
     * @param tableArgs The table arguments.
     */
    public void createTable(String tableName, String tableArgs) {
        queueQuery("CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableArgs + ")", Priority.HIGHEST);
    }

    /**
     * Sets the maximum allowed query failures.
     * If a query fails more than this value, It will be removed from the queue.
     * Default is set to 2.
     * @param failAttemptRemoval Allowed failures for a query.
     */
    public void setFailAttemptRemoval(int failAttemptRemoval) {
        this.failAttemptRemoval = failAttemptRemoval;
    }

    /**
     * Priority enum to use in queue.
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        HIGHEST
    }

    private void startQueue() {
        queueTask = new BukkitRunnable() {
            public void run() {
                List<Priority> priorities = new ArrayList<>(Arrays.asList(Priority.values()));
                Collections.reverse(priorities);

                for (Priority priority : priorities) {
                    List<SQLiteQuery> queries = queue.get(priority);
                    if (queries.isEmpty()) continue;

                    SQLiteQuery sqLiteQuery = queries.get(0);

                    try {
                        PreparedStatement preparedStatement = sqLiteQuery.createPreparedStatement(connection);
                        ResultSet resultSet = null;

                        if (sqLiteQuery.getStatement().startsWith("INSERT") ||
                                sqLiteQuery.getStatement().startsWith("UPDATE") ||
                                sqLiteQuery.getStatement().startsWith("DELETE") ||
                                sqLiteQuery.getStatement().startsWith("CREATE"))
                            preparedStatement.executeUpdate();
                        else
                            resultSet = preparedStatement.executeQuery();

                        sqLiteQuery.getCompletableFuture().complete(resultSet);
                        queries.remove(0);
                    } catch (SQLException e) {
                        Ruom.error("Failed to perform a query in the sqlite database. Stacktrace:");
                        Ruom.debug("Statement: " + sqLiteQuery.getStatement());
                        e.printStackTrace();

                        sqLiteQuery.increaseFailedAttempts();
                        if (sqLiteQuery.getFailedAttempts() > failAttemptRemoval) {
                            queries.remove(0);
                            Ruom.warn("This query has been removed from the queue as it exceeded the maximum failures." +
                                    " It's more likely to see some stuff break because of this failure, Please report" +
                                    " this bug to the developers.\n" +
                                    "Developer(s) of this project: " + RUoMPlugin.get().getDescription().getAuthors());
                        }
                    }
                    break;
                }

                tickQueue(this);
            }
        }.runTaskAsynchronously(RUoMPlugin.get());
    }

    private void tickQueue(BukkitRunnable queueRunnable) {
        Bukkit.getScheduler().runTaskLater(RUoMPlugin.get(), new Runnable() {
            @Override
            public void run() {
                queueRunnable.run();
            }
        }, 1);
    }

}
