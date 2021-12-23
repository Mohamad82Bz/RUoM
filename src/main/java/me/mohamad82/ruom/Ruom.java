package me.mohamad82.ruom;

import me.mohamad82.ruom.adventure.Adventure;
import me.mohamad82.ruom.areaselection.AreaSelectionListener;
import me.mohamad82.ruom.areaselection.AreaSelectionManager;
import me.mohamad82.ruom.events.packets.PacketListenerManager;
import me.mohamad82.ruom.gui.GUIListener;
import me.mohamad82.ruom.translators.skin.SkinBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class Ruom {

    private final static Set<String> recordedHasPluginSet = new HashSet<>();

    private static boolean debug = false;

    public static RUoMPlugin getPlugin() {
        return RUoMPlugin.get();
    }

    public static Server getServer() {
        return getPlugin().getServer();
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
        Adventure.initialize();
    }

    public static void initializeGUI() {
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(new GUIListener(), RUoMPlugin.get());
    }

    public static void initializeAreaSelection() {
        new AreaSelectionManager();
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(new AreaSelectionListener(), RUoMPlugin.get());
    }

    public static void initializeSkinBuilder() {
        new SkinBuilder();
    }

    public static void initializePacketListener() {
        PacketListenerManager.initialize();
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    public static void broadcast(Component message) {
        Adventure.get().players().sendMessage(message);
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
        try {
            Class.forName("net.kyori.adventure.platform.bukkit.BukkitAudiences");
            if (Adventure.get() != null) {
                Adventure.get().close();
            }
        } catch (ClassNotFoundException ignore) {}
        PacketListenerManager.shutdown();
    }

}
