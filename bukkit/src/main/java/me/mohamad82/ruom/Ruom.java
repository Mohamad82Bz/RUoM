package me.mohamad82.ruom;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.mohamad82.ruom.adventure.AdventureApi;
import me.mohamad82.ruom.event.packet.PacketListenerManager;
import me.mohamad82.ruom.gui.GUIListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class Ruom {

    private final static ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat(Ruom.getPlugin().getName().toLowerCase() + "-async-thread-%d").build();

    private final static ExecutorService asyncExecutor = Executors.newFixedThreadPool(RUoMPlugin.exclusiveThreads, THREAD_FACTORY);

    private final static Set<String> recordedHasPluginSet = new HashSet<>();

    private static boolean debug = false;

    public static RUoMPlugin getPlugin() {
        return RUoMPlugin.get();
    }

    public static Server getServer() {
        return getPlugin().getServer();
    }

    public static ConsoleCommandSender getConsoleSender() {
        return getServer().getConsoleSender();
    }

    public static boolean hasPlugin(String plugin) {
        if (recordedHasPluginSet.contains(plugin))
            return true;
        else {
            if (getServer().getPluginManager().getPlugin(plugin) != null &&
                    getServer().getPluginManager().isPluginEnabled(plugin)) {
                recordedHasPluginSet.add(plugin);
                return true;
            }
            else
                return false;
        }
    }

    public static Set<Player> getOnlinePlayers() {
        return new HashSet<>(getServer().getOnlinePlayers());
    }

    public static void setDebug(boolean debug) {
        Ruom.debug = debug;
    }

    public static void registerListener(Listener listener) {
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(listener, RUoMPlugin.get());
    }

    public static void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public static void initializeAdventure() {
        AdventureApi.initialize();
    }

    public static void initializeGUI() {
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(new GUIListener(), RUoMPlugin.get());
    }

    public static void initializePacketListener() {
        PacketListenerManager.initialize();
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    public static void broadcast(Component message) {
        AdventureApi.get().players().sendMessage(message);
    }

    public static void log(String message) {
        RUoMPlugin.get().getLogger().info(message);
    }

    public static void debug(String message) {
        if (debug) {
            log("[Debug] " + message);
        }
    }

    public static void warn(String message) {
        RUoMPlugin.get().getLogger().warning(message);
    }

    public static void error(String message) {
        RUoMPlugin.get().getLogger().severe(message);
    }

    public static BukkitTask runSync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(getPlugin(), runnable);
    }

    public static BukkitTask runSync(Runnable runnable, int delay) {
        return Bukkit.getScheduler().runTaskLater(getPlugin(), runnable, delay);
    }

    public static BukkitTask runSync(Runnable runnable, int delay, int period) {
        return Bukkit.getScheduler().runTaskTimer(getPlugin(), runnable, delay, period);
    }

    public static BukkitTask runAsync(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), runnable);
    }

    /**
     * Runs a task on exclusive asyc threads of the plugin. More reliable than bukkit async threads.
     * @param runnable The task
     * @return A future that returns null when the task got completed
     */
    public static Future<?> runEAsync(Runnable runnable) {
        return asyncExecutor.submit(runnable);
    }

    public static BukkitTask runAsync(Runnable runnable, int delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), runnable, delay);
    }

    public static BukkitTask runAsync(Runnable runnable, int delay, int period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, delay, period);
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

    public static void shutdown() {
        recordedHasPluginSet.clear();
        asyncExecutor.shutdown();
        try {
            if (AdventureApi.get() != null) {
                AdventureApi.get().close();
            }
        } catch (Exception ignore) {}
        try {
            PacketListenerManager.shutdown();
        } catch (Exception ignore) {}
        try {
            getServer().getMessenger().unregisterOutgoingPluginChannel(getPlugin());
            getServer().getMessenger().unregisterIncomingPluginChannel(getPlugin());
        } catch (Exception ignore) {}
    }

}
