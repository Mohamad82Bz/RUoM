package me.mohamad82.ruom.events.packets;

import io.netty.channel.*;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.events.packets.clientbound.AsyncClientboundPacketEvent;
import me.mohamad82.ruom.events.packets.clientbound.ClientboundPacketEvent;
import me.mohamad82.ruom.events.packets.serverbound.AsyncServerboundPacketEvent;
import me.mohamad82.ruom.events.packets.serverbound.ServerboundPacketEvent;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PacketListenerManager implements Listener {

    private static PacketListenerManager INSTANCE;

    protected static PacketListenerManager getInstance() {
        return INSTANCE;
    }

    public static void initialize() {
        if (INSTANCE == null) {
            INSTANCE = new PacketListenerManager();
            Ruom.getOnlinePlayers().forEach(INSTANCE::injectPlayer);
            Ruom.registerListener(INSTANCE);
        }
    }

    public static void shutdown() {
        if (INSTANCE != null) {
            Ruom.unregisterListener(INSTANCE);
            Ruom.getOnlinePlayers().forEach(INSTANCE::removePlayer);
            INSTANCE = null;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        injectPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    private void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet) {
                try {
                    PacketContainer packetContainer = new PacketContainer(packet);
                    ServerboundPacketEvent serverBoundPacketEvent = new ServerboundPacketEvent(player, packetContainer);
                    AsyncServerboundPacketEvent asyncServerBoundPacketEvent = new AsyncServerboundPacketEvent(player, packetContainer);

                    try {
                        Ruom.runSync(() -> Ruom.getServer().getPluginManager().callEvent(serverBoundPacketEvent));
                        Ruom.runAsync(() -> Ruom.getServer().getPluginManager().callEvent(asyncServerBoundPacketEvent));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!serverBoundPacketEvent.isCancelled()) {
                        try {
                            super.channelRead(context, packet);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Ruom.error("An error occured while handling (read) a packet. Please report this error to the plugin's author(s): " +
                                    Ruom.getPlugin().getDescription().getAuthors());
                        }
                    }
                } catch (IllegalArgumentException ignored) {}
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) {
                try {
                    PacketContainer packetContainer = new PacketContainer(packet);
                    ClientboundPacketEvent clientBoundPacketEvent = new ClientboundPacketEvent(player, packetContainer);
                    AsyncClientboundPacketEvent asyncClientBoundPacketEvent = new AsyncClientboundPacketEvent(player, packetContainer);

                    try {
                        Ruom.runSync(() -> Ruom.getServer().getPluginManager().callEvent(clientBoundPacketEvent));
                        Ruom.runAsync(() -> Ruom.getServer().getPluginManager().callEvent(asyncClientBoundPacketEvent));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!clientBoundPacketEvent.isCancelled()) {
                        try {
                            super.write(context, packet, channelPromise);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Ruom.error("An error occured while handling (write) a packet. Please report this error to the plugin's author(s): " +
                                    Ruom.getPlugin().getDescription().getAuthors());
                        }
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        };

        try {
            ChannelPipeline pipeline = NMSUtils.getChannel(player).pipeline();
            pipeline.addBefore(
                    "packet_handler",
                    String.format("%s_%s", Ruom.getPlugin().getDescription().getName(), player.getName()),
                    channelDuplexHandler
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(Player player) {
        try {
            Channel channel = NMSUtils.getChannel(player);
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(String.format("%s_%s", Ruom.getPlugin().getDescription().getName(), player.getName()));
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
