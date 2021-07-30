package me.Mohamad82.RUoM;

import me.Mohamad82.RUoM.areaselection.AreaSelectionListener;
import me.Mohamad82.RUoM.areaselection.AreaSelectionManager;
import me.Mohamad82.RUoM.gui.GUIListener;
import me.Mohamad82.RUoM.worldedit.enums.WEType;
import me.Mohamad82.RUoM.worldedit.WEManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RUoM {

    public void initializeGUI(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    public void initializeAreaSelectionManager(JavaPlugin plugin, ItemStack wand) {
        new AreaSelectionManager(wand);
        plugin.getServer().getPluginManager().registerEvents(new AreaSelectionListener(), plugin);
    }

    public void intializeWEManager(JavaPlugin plugin, WEType type) {
        new WEManager(plugin, type);
    }

}
