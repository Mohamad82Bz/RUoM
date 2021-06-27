package me.Mohamad82.RUoM;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class YamlConfig {

    private final JavaPlugin plugin;

    private final String fileName;

    private final boolean saveDefaults;

    private FileConfiguration config = null;
    private final File folder;
    private File configFile = null;

    public YamlConfig(JavaPlugin plugin, File folder, String fileName) {
        this(plugin, folder, fileName, true);
    }

    public YamlConfig(JavaPlugin plugin, File folder, String fileName, boolean saveDefaults) {
        this.plugin = plugin;
        this.folder = folder;
        this.saveDefaults = saveDefaults;

        this.fileName = fileName;

        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.configFile == null)
            this.configFile = new File(folder, fileName);

        this.config = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.config.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.config == null)
            reloadConfig();
        return this.config;
    }

    public void saveConfig() {
        if (this.config == null || this.configFile == null)
            return;

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (this.configFile == null)
            this.configFile = new File(folder, fileName);

        if (!(this.configFile.exists())) {
            try {
                this.plugin.saveResource(fileName, false);
            } catch (IllegalArgumentException e) {
                try {
                    this.configFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public String getFileName() {
        return fileName;
    }

}
