package me.mohamad82.ruom.messaging;

import com.google.gson.JsonObject;
import me.mohamad82.ruom.BRuom;
import me.mohamad82.ruom.utils.GsonUtils;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class BungeeMessagingChannel implements Listener {

    private final Set<BungeeMessagingEvent> messagingEvents = new HashSet<>();
    private final String name;

    public BungeeMessagingChannel(String namespace, String name) {
        this.name = namespace + ":" + name;
        BRuom.getServer().registerChannel(this.name);
    }

    public void register(BungeeMessagingEvent messagingEvent) {
        messagingEvents.add(messagingEvent);
    }

    public void unregister(BungeeMessagingEvent messagingEvent) {
        messagingEvents.remove(messagingEvent);
    }

    @EventHandler
    private void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals(name)) {
            String rawMessage = new String(event.getData(), StandardCharsets.UTF_8);
            JsonObject message = GsonUtils.getParser().parse(rawMessage.substring(2)).getAsJsonObject();

            for (BungeeMessagingEvent messagingEvent : messagingEvents) {
                messagingEvent.onPluginMessageReceived(event.getReceiver(), message);
            }
        }
    }

    public String getName() {
        return name;
    }

}
