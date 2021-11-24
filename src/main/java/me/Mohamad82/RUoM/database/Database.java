package me.Mohamad82.RUoM.database;

import me.Mohamad82.RUoM.Ruom;
import me.Mohamad82.RUoM.database.enums.Priority;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class Database {

    protected final Map<Priority, List<Query>> queue = new HashMap<>();

    protected int failAttemptRemoval = 2;
    protected BukkitTask queueTask;

    protected Database() {
        for (Priority priority : Priority.values()) {
            queue.put(priority, new ArrayList<>());
        }
    }

    protected abstract BukkitTask startQueue();

    protected void tickQueue(BukkitRunnable queueRunnable) {
        Ruom.runSync(queueRunnable, 1);
    }

    /**
     * Initializes the database connection.
     */
    public abstract void connect();

    /**
     * Shutdowns the database once queue becomes empty.
     * @return A completableFuture that will be completed once database shutdowns successfully.
     */
    public CompletableFuture<Boolean> scheduleShutdown() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        Ruom.runAsync(() -> {
            if (isQueueEmpty()) {
                shutdown();
                completableFuture.complete(true);
            }
        });
        return completableFuture;
    }

    /**
     * Force shutdowns the database and clears the queue.
     */
    public abstract void shutdown();

    /**
     * Queues a query.
     * @param query Statement that is going to run.
     * @param priority Priority of the query in queue. Higher priorities will be ran sooner in the queue.
     * @return Query class that contains CompletableFuture with ResultSet callback. Useful when you need the results of a query.
     * @see Query
     */
    public Query queueQuery(Query query, Priority priority) {
        queue.get(priority).add(query);
        return query;
    }

    /**
     * Queues a query with normal priority.
     * @param query Statement that is going to run.
     * @return Query class that contains CompletableFuture with ResultSet callback. Useful when you need the results of a query.
     * @see Query
     */
    public Query queueQuery(Query query) {
        queue.get(Priority.NORMAL).add(query);
        return query;
    }

    /**
     * Sets the maximum allowed query failures.
     * If a query fails more than this value, It will be removed from the queue.
     * Default is set to 2.
     * @param failAttemptRemoval Allowed failures for a query.
     */
    public void setFailAttemptRemoval(int failAttemptRemoval) {
        this.failAttemptRemoval = failAttemptRemoval;
    }

    /**
     * Returns whether queue is empty or not.
     * @return true if queue is empty
     */
    public boolean isQueueEmpty() {
        for (Priority priority : queue.keySet()) {
            if (!queue.get(priority).isEmpty())
                return false;
        }
        return true;
    }

}
