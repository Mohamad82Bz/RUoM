package me.mohamad82.ruom.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class Database {

    protected final Map<Priority, List<Query>> queue = new HashMap<>();

    protected int failAttemptRemoval = 2;

    protected Database() {
        for (Priority priority : Priority.values()) {
            queue.put(priority, new ArrayList<>());
        }
    }

    /**
     * Initializes the database connection.
     */
    public abstract void connect();

    /**
     * Shutdowns the database once queue becomes empty.
     * @return A completableFuture that will be completed once database shutdowns successfully.
     */
    public abstract CompletableFuture<Void> scheduleShutdown();

    /**
     * Force shutdowns the database and clears the queue.
     */
    public abstract void shutdown();

    /**
     * Queues a query.
     * @param query Statement that is going to run.
     * @param priority Priority of the query in queue. Higher priorities will be run sooner in the queue.
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

    public abstract void runQuery(Query query);

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
