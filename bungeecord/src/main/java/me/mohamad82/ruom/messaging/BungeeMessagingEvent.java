package me.mohamad82.ruom.messaging;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.connection.Connection;

public abstract class BungeeMessagingEvent {

    private final BungeeMessagingChannel channel;

    public BungeeMessagingEvent(BungeeMessagingChannel channel) {
        this.channel = channel;
        channel.register(this);
    }

    protected abstract void onPluginMessageReceived(Connection source, JsonObject message);

    public void unregister() {
        channel.unregister(this);
    }

    public BungeeMessagingChannel getChannel() {
        return channel;
    }

}
