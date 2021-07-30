package me.Mohamad82.RUoM.updater;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PluginUpdater {

    private final JavaPlugin mainPlugin;
    private File pluginFile;
    private final String pluginName;
    private final String downloadURL;

    public PluginUpdater(JavaPlugin mainPlugin, String pluginName, String downloadURL) {
        this.mainPlugin = mainPlugin;
        this.pluginName = pluginName;
        this.downloadURL = downloadURL;

        File pluginsFolder = new File("plugins");
        if (!pluginsFolder.isDirectory()) return;
        File[] plugins = pluginsFolder.listFiles();

        for (File plugin : plugins) {
            if (plugin.getName().contains(pluginName) && plugin.getName().contains(".jar")) {
                this.pluginFile = plugin;
                break;
            }
        }
    }


}
