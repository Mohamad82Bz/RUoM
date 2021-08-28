package me.Mohamad82.RUoM.database.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SQLiteQuery {

    private final Map<Integer, Object> statementValues = new HashMap<>();

    private final CompletableFuture<ResultSet> completableFuture;
    private final String statement;

    private int failedAttempts = 0;

    public SQLiteQuery(CompletableFuture<ResultSet> completableFuture, String statement) {
        this.completableFuture = completableFuture;
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    public CompletableFuture<ResultSet> getCompletableFuture() {
        return completableFuture;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void increaseFailedAttempts() {
        failedAttempts += 1;
    }

    public void setStatementValue(int index, Object value) {
        statementValues.put(index, value);
    }

    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(statement);

        for (int index : statementValues.keySet()) {
            Object value = statementValues.get(index);

            preparedStatement.setObject(index, value);
        }

        return preparedStatement;
    }

}
