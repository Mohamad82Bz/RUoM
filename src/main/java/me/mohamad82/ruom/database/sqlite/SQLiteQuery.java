package me.mohamad82.ruom.database.sqlite;

import me.mohamad82.ruom.database.Query;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public class SQLiteQuery extends Query {

    private SQLiteQuery(CompletableFuture<ResultSet> completableFuture, String statement) {
        super(statement);
    }

    public static SQLiteQuery sqLiteQuery(CompletableFuture<ResultSet> completableFuture, String statement) {
        return new SQLiteQuery(completableFuture, statement);
    }

}
