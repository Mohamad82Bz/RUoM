package me.Mohamad82.RUoM.database.sqlite;

import me.Mohamad82.RUoM.database.Query;

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
