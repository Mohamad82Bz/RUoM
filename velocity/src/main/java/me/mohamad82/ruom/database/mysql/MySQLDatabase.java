package me.mohamad82.ruom.database.mysql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.velocitypowered.api.scheduler.ScheduledTask;
import me.mohamad82.ruom.VRUoMPlugin;
import me.mohamad82.ruom.VRuom;
import me.mohamad82.ruom.database.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class MySQLDatabase extends MySQLExecutor {

    private final static ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat(VRuom.getDescription().name() + "-mysql-thread-%d").build();

    private final Class<?> mysqlDriver;
    private ScheduledTask queueTask;

    public MySQLDatabase(MySQLCredentials credentials, int poolingSize, Class<?> mysqlDriver) {
        super(credentials, poolingSize, THREAD_FACTORY);
        this.mysqlDriver = mysqlDriver;
    }

    @Override
    public void connect() {
        super.connect(mysqlDriver.getName());
        this.queueTask = startQueue();
    }

    @Override
    public void shutdown() {
        queueTask.cancel();
        queue.clear();
        hikari.close();
    }

    @Override
    public CompletableFuture<Void> scheduleShutdown() {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        VRuom.runAsync(() -> {
            if (isQueueEmpty()) {
                shutdown();
                completableFuture.complete(null);
            }
        }, 0, TimeUnit.SECONDS, 50, TimeUnit.MILLISECONDS);
        return completableFuture;
    }

    public ScheduledTask startQueue() {
        Runnable runnable = new Runnable() {
            public void run() {
                if (poolingUsed >= poolingSize) {
                    tick(this);
                    return;
                }

                tick();

                tick(this);
            }
        };
        return VRuom.getServer().getScheduler().buildTask(VRUoMPlugin.get(), runnable).schedule();
    }

    public void tick(Runnable runnable) {
        VRuom.runAsync(runnable, 10, TimeUnit.MILLISECONDS);
    }

    private Connection createConnection() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            VRuom.error("Failed to establish mysql connection!");
            e.printStackTrace();
            return null;
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            VRuom.error("Failed to close a mysql connection!");
            e.printStackTrace();
        }
    }

    @Override
    protected void onQueryFail(Query query) {

    }

    @Override
    protected void onQueryRemoveDueToFail(Query query) {

    }

}
