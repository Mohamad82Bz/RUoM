package me.Mohamad82.RUoM;

import me.Mohamad82.RUoM.areaselection.AreaSelectionListener;
import me.Mohamad82.RUoM.areaselection.AreaSelectionManager;
import me.Mohamad82.RUoM.gui.GUIListener;
import me.Mohamad82.RUoM.worldedit.enums.WEType;
import me.Mohamad82.RUoM.worldedit.WEManager;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Ruom {

    public static boolean debug = false;

    public static void setDebug(boolean newDebug) {
        debug = newDebug;
    }

    public static void registerEvents(Listener listener) {
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(listener, RUoMPlugin.get());
    }

    public static void initializeGUI() {
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(new GUIListener(), RUoMPlugin.get());
    }

    public static void initializeAreaSelection(ItemStack wand) {
        new AreaSelectionManager(wand);
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(new AreaSelectionListener(), RUoMPlugin.get());
    }

    public static void intializeWorldEdit(JavaPlugin plugin, WEType type) {
        new WEManager(plugin, type);
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

}
