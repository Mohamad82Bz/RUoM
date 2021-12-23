package me.mohamad82.ruom.database.sqlite;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.database.Database;
import me.mohamad82.ruom.database.Query;
import me.mohamad82.ruom.database.enums.Priority;
import me.mohamad82.ruom.database.sqlite.exceptions.SQLiteException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLiteDatabase extends Database {

    private final File dbFile;
    private Connection connection;

    public SQLiteDatabase(File dbFile) {
        this.dbFile = dbFile;
        try {
            if (!dbFile.exists())
                dbFile.createNewFile();
        } catch (IOException e) {
            Ruom.error("Failed to create the sqlite database file. Stacktrace:");
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
            startQueue();
        } catch (SQLException e) {
            throw new SQLiteException(e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        try {
            connection.close();
            queue.clear();
            queueTask.cancel();
        } catch (SQLException e) {
            throw new SQLiteException(e.getMessage());
        }
    }

    @Override
    protected BukkitTask startQueue() {
        return new BukkitRunnable() {
            public void run() {
                List<Priority> priorities = new ArrayList<>(Arrays.asList(Priority.values()));

                for (Priority priority : priorities) {
                    List<Query> queries = queue.get(priority);
                    if (queries.isEmpty()) continue;

                    Query query = queries.get(0);

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
                        queries.remove(0);
                    } catch (SQLException e) {
                        Ruom.error("Failed to perform a query in the sqlite database. Stacktrace:");
                        Ruom.debug("Statement: " + query.getStatement());
                        e.printStackTrace();

                        query.increaseFailedAttempts();
                        if (query.getFailedAttempts() > failAttemptRemoval) {
                            queries.remove(0);
                            Ruom.warn("This query has been removed from the sqlite queue as it exceeded the maximum failures." +
                                    " It's more likely to see some stuff break because of this failure, Please report" +
                                    " this bug to the developers.\n" +
                                    "Developer(s) of this plugin: " + Ruom.getPlugin().getDescription().getAuthors());
                        }
                    }
                    break;
                }

                tickQueue(this);
            }
        }.runTaskAsynchronously(Ruom.getPlugin());
    }

}
