package me.Mohamad82.RUoM.events.packets;

import com.cryptomorin.xseries.ReflectionUtils;
import io.netty.channel.*;
import me.Mohamad82.RUoM.Ruom;
import me.Mohamad82.RUoM.events.packets.clientbound.AsyncClientBoundPacketEvent;
import me.Mohamad82.RUoM.events.packets.clientbound.ClientBoundPacketEvent;
import me.Mohamad82.RUoM.events.packets.serverbound.AsyncServerBoundPacketEvent;
import me.Mohamad82.RUoM.events.packets.serverbound.ServerBoundPacketEvent;
import me.Mohamad82.RUoM.utils.ServerVersion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;

public class PacketListenerManager implements Listener {

    private static Class<?> PLAYER_CONNECTION, NETWORK_MANAGER;

    private static Field PLAYER_CONNECTION_NETWORK_MANAGER_FIELD, NETWORK_MANAGER_CHANNEL_FIELD;

    static {
        try {
            {
                PLAYER_CONNECTION = ReflectionUtils.getNMSClass("server.network", "PlayerConnection");
                NETWORK_MANAGER = ReflectionUtils.getNMSClass("network", "NetworkManager");
            }
            {
                PLAYER_CONNECTION_NETWORK_MANAGER_FIELD = PLAYER_CONNECTION.getField(ServerVersion.supports(17) ? "a" : "networkManager");
                NETWORK_MANAGER_CHANNEL_FIELD = NETWORK_MANAGER.getField(ServerVersion.supports(17) ? "k" : "channel");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Ruom.error("An error occured while initializing the packet listener. This is (probably) because of version incompatibility," +
                    " Please make sure that this plugin supports your server version: " + Ruom.getServer().getVersion());
            Ruom.error("If you think this is a mistake, please report this to the plugin's author(s): " + Ruom.getPlugin().getDescription().getAuthors());
        }
    }

    private static PacketListenerManager instance;

    protected static PacketListenerManager getInstance() {
        return instance;
    }

    public static void initialize() {
        if (instance == null) {
            instance = new PacketListenerManager();
            Ruom.getOnlinePlayers().forEach(instance::injectPlayer);
            Ruom.registerListener(instance);
        }
    }

    public static void shutdown() {
        if (instance != null) {
            Ruom.unregisterListener(instance);
            Ruom.getOnlinePlayers().forEach(instance::removePlayer);
            instance = null;
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
                ServerBoundPacketEvent serverBoundPacketEvent = new ServerBoundPacketEvent(player, new PacketContainer(packet));
                AsyncServerBoundPacketEvent asyncServerBoundPacketEvent = new AsyncServerBoundPacketEvent(player, new PacketContainer(packet));

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
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) {
                ClientBoundPacketEvent clientBoundPacketEvent = new ClientBoundPacketEvent(player, new PacketContainer(packet));
                AsyncClientBoundPacketEvent asyncClientBoundPacketEvent = new AsyncClientBoundPacketEvent(player, new PacketContainer(packet));

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
            }
        };

        try {
            ChannelPipeline pipeline = getChannel(player).pipeline();
            pipeline.addBefore(
                    "packet_handler",
                    String.format("%s_%s", Ruom.getPlugin().getDescription().getName(), player.getName()),
                    channelDuplexHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(Player player) {
        try {
            Channel channel = getChannel(player);
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(String.format("%s_%s", Ruom.getPlugin().getDescription().getName(), player.getName()));
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Channel getChannel(Player player) throws IllegalAccessException {
        Object playerConnection = ReflectionUtils.getConnection(player);
        return (Channel) NETWORK_MANAGER_CHANNEL_FIELD.get(PLAYER_CONNECTION_NETWORK_MANAGER_FIELD.get(playerConnection));
    }

}
