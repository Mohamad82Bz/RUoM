package me.mohamad82.ruom.database;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.mohamad82.ruom.database.mysql.MySQLCredentials;
import me.mohamad82.ruom.database.mysql.MySQLExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;

public class MySQLDatabase extends MySQLExecutor {

    private final static ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("mysql-thread-%d").build();

    public MySQLDatabase(MySQLCredentials credentials, int poolingSize) {
        super(credentials, poolingSize, THREAD_FACTORY);
    }

    @Override
    public void connect() {
        super.connect("com.mysql.cj.jdbc.Driver");
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
        queue.clear();
        hikari.close();
    }

    private void startQueue() {
        new Thread(() -> {
            while (!isQueueEmpty()) {
                if (poolingUsed <= poolingSize) {
                    tick();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
