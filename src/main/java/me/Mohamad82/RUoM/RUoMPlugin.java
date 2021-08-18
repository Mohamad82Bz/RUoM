package me.Mohamad82.RUoM;

import org.bukkit.plugin.java.JavaPlugin;

public class RUoMPlugin extends JavaPlugin {

    private static RUoMPlugin instance;

    public static RUoMPlugin get() {
        return instance;
    }

    public RUoMPlugin() {
        instance = this;
    }

}
