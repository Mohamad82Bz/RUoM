package me.mohamad82.ruom;

import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

public class VRUoMPlugin {

    private static VRUoMPlugin instance;
    private static ProxyServer server;
    private static Logger logger;

    public VRUoMPlugin(ProxyServer server, Logger logger) {
        instance = this;
        VRUoMPlugin.server = server;
        VRUoMPlugin.logger = logger;
    }

    public static VRUoMPlugin get() {
        return instance;
    }

    public static ProxyServer getServer() {
        return server;
    }

    public static Logger getLogger() {
        return logger;
    }

}
