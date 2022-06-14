package me.mohamad82.ruom.database.sqlite;

import me.mohamad82.ruom.database.Database;
import me.mohamad82.ruom.database.Priority;
import me.mohamad82.ruom.database.Query;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public abstract class SQLiteExecutor extends Database {

    protected final File dbFile;
    private final Logger logger;
    protected Connection connection;

    protected SQLiteExecutor(File dbFile, @Nullable Logger logger) {
        this.dbFile = dbFile;
        this.logger = logger;
        try {
            if (!dbFile.exists())
                dbFile.createNewFile();
        } catch (IOException e) {
            if (logger != null) {
                logger.severe("Failed to create the sqlite database file. Stacktrace:");
            }
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
        } catch (SQLException e) {
            if (logger != null) {
                logger.severe(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    protected void tick() {
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
                onQueryFail(query);
                e.printStackTrace();

                query.increaseFailedAttempts();
                if (query.getFailedAttempts() > failAttemptRemoval) {
                    queries.remove(0);
                    onQueryRemoveDueToFail(query);
                }
            }
            break;
        }
    }

    protected abstract void onQueryFail(Query query);

    protected abstract void onQueryRemoveDueToFail(Query query);

}
