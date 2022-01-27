package me.mohamad82.ruom.messaging;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelMessageSource;

public abstract class VelocityMessagingEvent {

    private final VelocityMessagingChannel channel;

    public VelocityMessagingEvent(VelocityMessagingChannel channel) {
        this.channel = channel;
        channel.register(this);
    }

    protected abstract void onPluginMessageReceived(ChannelMessageSource source, JsonObject message);

    public void unregister() {
        channel.unregister(this);
    }

    public VelocityMessagingChannel getChannel() {
        return channel;
    }

}
