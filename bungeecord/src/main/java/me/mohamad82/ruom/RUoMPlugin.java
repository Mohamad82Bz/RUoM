package me.mohamad82.ruom;

import net.md_5.bungee.api.plugin.Plugin;

public class RUoMPlugin extends Plugin {

    private static RUoMPlugin instance;

    public RUoMPlugin() {
        instance = this;
    }

    public static RUoMPlugin get() {
        return instance;
    }

}
