package me.mohamad82.ruom;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Ruom {

    private static boolean debug = false;

    public static RUoMPlugin getPlugin() {
        return RUoMPlugin.get();
    }

    public static ProxyServer getServer() {
        return getPlugin().getProxy();
    }

    public static void registerListener(Listener listener) {
        getServer().getPluginManager().registerListener(getPlugin(), listener);
    }

    public static Collection<ProxiedPlayer> getOnlinePlayers() {
        return getServer().getPlayers();
    }

    public static Optional<ProxiedPlayer> getPlayer(String username) {
        for (ProxiedPlayer player : getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(username)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    public static void setDebug(boolean debug) {
        Ruom.debug = debug;
    }

    public static void log(String message) {
        getPlugin().getLogger().info(message);
    }

    public static void debug(String message) {
        if (debug) {
            log("[Debug] " + message);
        }
    }

    public static void warn(String message) {
        getPlugin().getLogger().warning(message);
    }

    public static void error(String message) {
        getPlugin().getLogger().severe(message);
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
        return getServer().getScheduler().runAsync(getPlugin(), runnable);
    }

    public static ScheduledTask runAsync(Runnable runnable, long delay, TimeUnit delayUnit) {
        return getServer().getScheduler().schedule(getPlugin(), runnable, delay, delayUnit);
    }

    public static ScheduledTask runAsync(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return getServer().getScheduler().schedule(getPlugin(), runnable, delay, period, timeUnit);
    }

}
