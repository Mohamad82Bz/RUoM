package me.Mohamad82.RUoM;

import me.Mohamad82.RUoM.adventureapi.AdventureAPIManager;
import me.Mohamad82.RUoM.areaselection.AreaSelectionListener;
import me.Mohamad82.RUoM.areaselection.AreaSelectionManager;
import me.Mohamad82.RUoM.gui.GUIListener;
import me.Mohamad82.RUoM.worldedit.WEManager;
import me.Mohamad82.RUoM.worldedit.enums.WEType;
import org.bukkit.event.Listener;

public class Ruom {

    private static boolean debug = false;

    public static RUoMPlugin getPlugin() {
        return RUoMPlugin.get();
    }

    public static void setDebug(boolean debug) {
        Ruom.debug = debug;
    }

    public static void registerListener(Listener listener) {
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(listener, RUoMPlugin.get());
    }

    public static void initializeAdventure() {
        AdventureAPIManager.initialize();
    }

    public static void initializeGUI() {
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(new GUIListener(), RUoMPlugin.get());
    }

    public static void initializeAreaSelection() {
        new AreaSelectionManager();
        RUoMPlugin.get().getServer().getPluginManager().registerEvents(new AreaSelectionListener(), RUoMPlugin.get());
    }

    public static void intializeWorldEdit(WEType type) {
        new WEManager(RUoMPlugin.get(), type);
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

    public static void shutdown() {
        if (AdventureAPIManager.getAdventure() != null) {
            AdventureAPIManager.getAdventure().close();
        }
    }

}
