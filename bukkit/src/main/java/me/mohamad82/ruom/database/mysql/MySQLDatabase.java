package me.mohamad82.ruom.database.mysql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.database.Query;
import me.mohamad82.ruom.utils.ListUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;

public class MySQLDatabase extends MySQLExecutor {

    private final static ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat(Ruom.getPlugin().getName().toLowerCase() + "-mysql-thread-%d").build();

    private final static List<String> mysqlDrivers = ListUtils.toList(
            "com.mysql.cj.jdbc.Driver",
            "com.mysql.jdbc.Driver"
    );

    private BukkitTask queueTask;

    private String mysqlDriverClass;

    public MySQLDatabase(MySQLCredentials credentials, int poolingSize) {
        super(credentials, poolingSize, THREAD_FACTORY);
    }

    @Override
    public void connect() {
        for (String mysqlDriver : mysqlDrivers) {
            try {
                Class.forName(mysqlDriver);
                this.mysqlDriverClass = mysqlDriver;
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (mysqlDriverClass == null) {
            throw new IllegalStateException("Failed to find mysql driver!");
        }
        super.connect(mysqlDriverClass);
        this.queueTask = startQueue();
    }

    @Override
    public void shutdown() {
        queueTask.cancel();
        queue.clear();
        hikari.close();
    }

    public void setDriverClassName(String driverClassName) {
        this.mysqlDriverClass = driverClassName;
    }

    @Override
    public CompletableFuture<Void> scheduleShutdown() {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        Ruom.runAsync(() -> {
            if (isQueueEmpty()) {
                shutdown();
                completableFuture.complete(null);
            }
        }, 0, 1);
        return completableFuture;
    }

    public BukkitTask startQueue() {
        return new BukkitRunnable() {
            public void run() {
                if (poolingUsed >= poolingSize) {
                    tick(this);
                    return;
                }

                tick();

                tick(this);
            }
        }.runTask(Ruom.getPlugin());
    }

    public void tick(Runnable runnable) {
        Ruom.runSync(runnable, 1);
    }

    private Connection createConnection() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            Ruom.error("Failed to establish mysql connection!");
            e.printStackTrace();
            return null;
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            Ruom.error("Failed to close a mysql connection!");
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
