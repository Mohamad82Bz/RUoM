package me.mohamad82.ruom.database.sqlite;

import me.mohamad82.ruom.database.Query;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class SQLiteDatabase extends SQLiteExecutor {

    protected SQLiteDatabase(File dbFile, @Nullable Logger logger) {
        super(dbFile, logger);
    }

    @Override
    public void connect() {
        super.connect();
        startQueue();
    }

    @Override
    public CompletableFuture<Void> scheduleShutdown() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        new Thread(() -> {
            while (!isQueueEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            shutdown();
            future.complete(null);
        }).start();
        return future;
    }

    @Override
    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startQueue() {
        new Thread(() -> {
            while (!isQueueEmpty()) {
                tick();
                try {
                    //noinspection BusyWait
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onQueryFail(Query query) {
        //ignored
    }

    @Override
    protected void onQueryRemoveDueToFail(Query query) {
        //ignored
    }

}
