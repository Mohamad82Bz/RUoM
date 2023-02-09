package me.mohamad82.ruom;

import org.bukkit.plugin.java.JavaPlugin;

public class RUoMPlugin extends JavaPlugin {

    private static RUoMPlugin instance;
    protected static int exclusiveThreads = 1;

    public RUoMPlugin() {
        instance = this;
    }

    public RUoMPlugin(int exclusiveThreads) {
        instance = this;
        RUoMPlugin.exclusiveThreads = exclusiveThreads;
    }

    public static RUoMPlugin get() {
        return instance;
    }

}
