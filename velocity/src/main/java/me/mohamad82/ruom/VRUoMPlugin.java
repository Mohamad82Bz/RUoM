package me.mohamad82.ruom;

import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

public class VRUoMPlugin {

    private static VRUoMPlugin instance;
    private static Object plugin;
    private static ProxyServer server;
    private static Logger logger;

    public VRUoMPlugin(Object plugin, ProxyServer server, Logger logger) {
        instance = this;
        VRUoMPlugin.plugin = plugin;
        VRUoMPlugin.server = server;
        VRUoMPlugin.logger = logger;
    }

    public static VRUoMPlugin get() {
        return instance;
    }

    public static Object getPlugin() {
        return plugin;
    }

    public static ProxyServer getServer() {
        return server;
    }

    public static Logger getLogger() {
        return logger;
    }

}
