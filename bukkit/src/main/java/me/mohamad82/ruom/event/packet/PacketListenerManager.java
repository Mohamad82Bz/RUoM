package me.mohamad82.ruom.event.packet;

import io.netty.channel.*;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.ClientboundContainerSetContentPacketAccessor;
import me.mohamad82.ruom.nmsaccessors.ClientboundContainerSetSlotPacketAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerboundInteractPacketAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerboundPlayerActionPacketAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PacketListenerManager implements Listener {

    private static PacketListenerManager INSTANCE;

    protected static PacketListenerManager getInstance() {
        if (INSTANCE == null) initialize();
        return INSTANCE;
    }

    private final Set<PacketEvent> packetEvents = new HashSet<>();

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

    protected void unregister(PacketEvent packetEvent) {
        packetEvents.remove(packetEvent);
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
                        if (packet.getClass().equals(ServerboundPlayerActionPacketAccessor.getType()) && !PlayerActionEvent.HANDLER_LIST.isEmpty()) {
                            Ruom.runAsync(() -> {
                                PlayerActionEvent.HANDLER_LIST.forEach(event -> event.handle(player, packet));
                            });
                        } else if (packet.getClass().equals(ServerboundInteractPacketAccessor.getType()) && !PlayerInteractAtEntityEvent.HANDLER_LIST.isEmpty()) {
                            Ruom.runAsync(() -> {
                                PlayerInteractAtEntityEvent.HANDLER_LIST.forEach(event -> event.handle(player, packet));
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

                    if ((packet.getClass().equals(ClientboundContainerSetSlotPacketAccessor.getType()) ||
                            packet.getClass().equals(ClientboundContainerSetContentPacketAccessor.getType())) && !ContainerItemEvent.HANDLER_LIST.isEmpty()) {
                        ContainerItemEvent.HANDLER_LIST.forEach(event -> event.handle(player, packet));
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
