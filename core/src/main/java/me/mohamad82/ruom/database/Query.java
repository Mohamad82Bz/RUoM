package me.mohamad82.ruom.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Query {

    protected final Map<Integer, Object> statementValues = new HashMap<>();
    protected final Set<Query> requirements = new HashSet<>();
    protected final CompletableFuture<ResultSet> completableFuture;
    protected final String statement;
    protected int failedAttempts = 0;

    private int statusCode = StatusCode.NOT_STARTED.getCode();

    protected Query(String statement) {
        this.completableFuture = new CompletableFuture<>();
        this.statement = statement;
    }

    public static Query query(String statement) {
        return new Query(statement);
    }

    public Query addRequirement(Query query) {
        requirements.add(query);
        return this;
    }

    public Set<Query> getRequirements() {
        return requirements;
    }

    public boolean hasDoneRequirements() {
        boolean hasDoneRequirements = true;
        for (Query query : requirements) {
            if (query.getStatusCode() != StatusCode.FINISHED.getCode()) {
                hasDoneRequirements = false;
                break;
            }
        }
        return hasDoneRequirements;
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

    public Query setStatementValue(int index, Object value) {
        statementValues.put(index, value);
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(statement);

        for (int index : statementValues.keySet()) {
            Object value = statementValues.get(index);

            preparedStatement.setObject(index, value);
        }

        return preparedStatement;
    }

    public enum StatusCode {
        NOT_STARTED(-1),
        RUNNING(0),
        FAILED(1),
        FINISHED(2);

        private final int code;

        StatusCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

}
