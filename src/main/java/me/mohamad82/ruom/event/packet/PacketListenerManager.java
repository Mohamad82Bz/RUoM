package me.mohamad82.ruom.event.packet;

import io.netty.channel.*;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.DirectionAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerboundPlayerActionPacketAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerboundPlayerActionPacket_i_ActionAccessor;
import me.mohamad82.ruom.nmsaccessors.Vec3iAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.vector.Vector3;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class PacketListenerManager implements Listener {

    private static PacketListenerManager INSTANCE;

    protected static PacketListenerManager getInstance() {
        if (INSTANCE == null) initialize();
        return INSTANCE;
    }

    private final Set<PacketEvent> packetEvents = new HashSet<>();
    private final Set<PlayerDigEvent> digEvents = new HashSet<>();

    private PacketListenerManager() {

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

    protected void register(PacketEvent packetEvent) {
        packetEvents.add(packetEvent);
    }

    protected void register(PlayerDigEvent digEvent) {
        digEvents.add(digEvent);
    }

    protected void unregister(PacketEvent packetEvent) {
        packetEvents.remove(packetEvent);
    }

    protected void unregister(PlayerDigEvent digEvent) {
        digEvents.remove(digEvent);
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
                    boolean isCancelled = false;

                    for (PacketEvent packetEvent : packetEvents) {
                        try {
                            isCancelled = !packetEvent.onServerboundPacket(player, packetContainer);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Ruom.error("An error occured while handling (reading) a packet. Please report this error to the plugin's author(s): " +
                                    Ruom.getPlugin().getDescription().getAuthors());
                        }
                    }

                    if (!isCancelled) {
                        if (packet.getClass().equals(ServerboundPlayerActionPacketAccessor.getType())) {
                            Ruom.runAsync(() -> {
                                Ruom.run(() -> {
                                    Object action = ServerboundPlayerActionPacketAccessor.getMethodGetAction1().invoke(packet);
                                    Object nmsBlockPos = ServerboundPlayerActionPacketAccessor.getMethodGetPos1().invoke(packet);
                                    Object nmsDirection = ServerboundPlayerActionPacketAccessor.getMethodGetDirection1().invoke(packet);

                                    PlayerDigEvent.Direction direction = PlayerDigEvent.Direction.valueOf(((String) DirectionAccessor.getMethodGetName1().invoke(nmsDirection)).toUpperCase());
                                    Vector3 blockPos = Vector3.at(
                                            (int) Vec3iAccessor.getMethodGetX1().invoke(nmsBlockPos),
                                            (int) Vec3iAccessor.getMethodGetY1().invoke(nmsBlockPos),
                                            (int) Vec3iAccessor.getMethodGetZ1().invoke(nmsBlockPos)
                                    );

                                    if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldSTART_DESTROY_BLOCK())) {
                                        for (PlayerDigEvent digEvent : digEvents) {
                                            digEvent.onStartDig(player, blockPos, direction);
                                        }
                                    } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldSTOP_DESTROY_BLOCK()) || action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldABORT_DESTROY_BLOCK())) {
                                        for (PlayerDigEvent digEvent : digEvents) {
                                            digEvent.onStopDig(player, blockPos);
                                        }
                                    }
                                });
                            });
                        }
                        try {
                            super.channelRead(context, packet);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IllegalArgumentException ignored) {}
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) {
                try {
                    PacketContainer packetContainer = new PacketContainer(packet);
                    boolean isCancelled = false;

                    for (PacketEvent packetEvent : packetEvents) {
                        try {
                            isCancelled = !packetEvent.onClientboundPacket(player, packetContainer);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Ruom.error("An error occured while handling (writing) a packet. Please report this error to the plugin's author(s): " +
                                    Ruom.getPlugin().getDescription().getAuthors());
                        }
                    }

                    if (!isCancelled) {
                        try {
                            super.write(context, packet, channelPromise);
                        } catch (Exception e) {
                            e.printStackTrace();
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
