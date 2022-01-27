package me.mohamad82.ruom;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Ruom {

    private static Object MAIN_INSTANCE;
    private static ProxyServer proxyServer;
    private static Logger logger;
    private static boolean debug = false;

    public static void initialize(Object plugin, ProxyServer server, Logger logger) {
        MAIN_INSTANCE = plugin;
        proxyServer = server;
        Ruom.logger = logger;
    }

    public static Object getPlugin() {
        return MAIN_INSTANCE;
    }

    public static ProxyServer getServer() {
        return proxyServer;
    }

    public static void registerListener(Object listener) {
        getServer().getEventManager().register(getPlugin(), listener);
    }

    public static Collection<Player> getOnlinePlayers() {
        return getServer().getAllPlayers();
    }

    public static Optional<Player> getPlayer(String username) {
        for (Player player : getOnlinePlayers()) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    public static void setDebug(boolean debug) {
        Ruom.debug = debug;
    }

    public static void log(String message) {
        logger.info(message);
    }

    public static void debug(String message) {
        if (debug) {
            log("[Debug] " + message);
        }
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void run(RunnableExc runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface public interface RunnableExc {
        void run() throws Exception;
    }

    public static ScheduledTask runAsync(Runnable runnable) {
        return getServer().getScheduler().buildTask(getPlugin(), runnable).schedule();
    }

    public static ScheduledTask runAsync(Runnable runnable, long delay, TimeUnit delayUnit) {
        return getServer().getScheduler().buildTask(getPlugin(), runnable).delay(delay, delayUnit).schedule();
    }

    public static ScheduledTask runAsync(Runnable runnable, long delay, TimeUnit delayUnit, long period, TimeUnit periodUnit) {
        return getServer().getScheduler().buildTask(getPlugin(), runnable).delay(delay, delayUnit).repeat(period, periodUnit).schedule();
    }

}
