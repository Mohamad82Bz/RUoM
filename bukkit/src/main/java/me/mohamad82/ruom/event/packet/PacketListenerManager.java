package me.mohamad82.ruom.event.packet;

import io.netty.channel.*;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.npc.LivingEntityNPC;
import me.mohamad82.ruom.utils.ListUtils;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import me.mohamad82.ruom.math.vector.Vector3;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PacketListenerManager implements Listener {

    private static PacketListenerManager INSTANCE;

    protected static PacketListenerManager getInstance() {
        if (INSTANCE == null) initialize();
        return INSTANCE;
    }

    private final Set<PacketEvent> packetEvents = new HashSet<>();
    private final Set<PlayerActionEvent> actionEvents = new HashSet<>();
    private final Set<PlayerInteractAtEntityEvent> interactEvents = new HashSet<>();
    private final Set<ChatPreviewEvent> chatPreviewEvents = new HashSet<>();

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

    protected void register(PlayerActionEvent actionEvent) {
        actionEvents.add(actionEvent);
    }

    protected void register(PlayerInteractAtEntityEvent interactEvent) {
        interactEvents.add(interactEvent);
    }

    protected void register(ChatPreviewEvent chatPreviewEvent) {
        chatPreviewEvents.add(chatPreviewEvent);
    }

    protected void unregister(PacketEvent packetEvent) {
        packetEvents.remove(packetEvent);
    }

    protected void unregister(PlayerActionEvent actionEvent) {
        actionEvents.remove(actionEvent);
    }

    protected void unregister(PlayerInteractAtEntityEvent interactEvent) {
        interactEvents.remove(interactEvent);
    }

    protected void unregister(ChatPreviewEvent chatPreviewEvent) {
        chatPreviewEvents.remove(chatPreviewEvent);
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
                        if (packet.getClass().equals(ServerboundPlayerActionPacketAccessor.getType()) && !actionEvents.isEmpty()) {
                            Ruom.runAsync(() -> {
                                try {
                                    Object action = ServerboundPlayerActionPacketAccessor.getMethodGetAction1().invoke(packet);
                                    Object nmsBlockPos = ServerboundPlayerActionPacketAccessor.getMethodGetPos1().invoke(packet);
                                    Object nmsDirection = ServerboundPlayerActionPacketAccessor.getMethodGetDirection1().invoke(packet);

                                    PlayerActionEvent.Direction direction = PlayerActionEvent.Direction.valueOf(((String) DirectionAccessor.getMethodGetName1().invoke(nmsDirection)).toUpperCase());
                                    Vector3 blockPos = Vector3.at(
                                            (int) Vec3iAccessor.getMethodGetX1().invoke(nmsBlockPos),
                                            (int) Vec3iAccessor.getMethodGetY1().invoke(nmsBlockPos),
                                            (int) Vec3iAccessor.getMethodGetZ1().invoke(nmsBlockPos)
                                    );

                                    if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldSTART_DESTROY_BLOCK())) {
                                        actionEvents.forEach(actionEvent -> actionEvent.onStartDig(player, blockPos, direction));
                                    } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldSTOP_DESTROY_BLOCK()) || action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldABORT_DESTROY_BLOCK())) {
                                        actionEvents.forEach(actionEvent -> actionEvent.onStopDig(player, blockPos));
                                    } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldDROP_ALL_ITEMS())) {
                                        actionEvents.forEach(actionEvent -> actionEvent.onDropAllItems(player));
                                    } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldDROP_ITEM())) {
                                        actionEvents.forEach(actionEvent -> actionEvent.onDropItem(player));
                                    } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldRELEASE_USE_ITEM())) {
                                        actionEvents.forEach(actionEvent -> actionEvent.onUseItemRelease(player));
                                    } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldSWAP_ITEM_WITH_OFFHAND())) {
                                        actionEvents.forEach(actionEvent -> actionEvent.onSwapItemsWithOffHand(player));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else if (packet.getClass().equals(ServerboundInteractPacketAccessor.getType()) && !interactEvents.isEmpty()) {
                            Ruom.runAsync(() -> {
                                try {
                                    int entityId = (int) ServerboundInteractPacketAccessor.getFieldEntityId().get(packet);
                                    Object action = ServerboundInteractPacketAccessor.getFieldAction().get(packet);
                                    int actionId = -1;
                                    LivingEntityNPC.InteractionHand hand = null;
                                    Vector3 location = null;

                                    if (ServerVersion.supports(17)) {
                                        Object actionType = ServerboundInteractPacket_i_ActionAccessor.getMethodGetType1().invoke(action);
                                        if (actionType.equals(ServerboundInteractPacket_i_ActionTypeAccessor.getFieldATTACK())) {
                                            actionId = 0;
                                        } else if (actionType.equals(ServerboundInteractPacket_i_ActionTypeAccessor.getFieldINTERACT())) {
                                            actionId = 1;
                                            hand = LivingEntityNPC.InteractionHand.fromNmsObject(ServerboundInteractPacket_i_InteractionActionAccessor.getFieldHand().get(action));
                                        } else if (actionType.equals(ServerboundInteractPacket_i_ActionTypeAccessor.getFieldINTERACT_AT())) {
                                            actionId = 2;
                                            hand = LivingEntityNPC.InteractionHand.fromNmsObject(ServerboundInteractPacket_i_InteractionAtLocationActionAccessor.getFieldHand().get(action));
                                            Object vec3 = ServerboundInteractPacket_i_InteractionAtLocationActionAccessor.getFieldLocation().get(action);
                                            location = Vector3.at(
                                                    (double) Vec3Accessor.getMethodX1().invoke(vec3),
                                                    (double) Vec3Accessor.getMethodY1().invoke(vec3),
                                                    (double) Vec3Accessor.getMethodZ1().invoke(vec3)
                                            );
                                        }
                                    } else {
                                        if (action.equals(ServerboundInteractPacket_i_ActionAccessor.getFieldINTERACT().get(null))) {
                                            actionId = 1;
                                            hand = LivingEntityNPC.InteractionHand.fromNmsObject(ServerboundInteractPacketAccessor.getFieldHand().get(packet));
                                        } else if (action.equals(ServerboundInteractPacket_i_ActionAccessor.getFieldINTERACT_AT().get(null))) {
                                            actionId = 2;
                                            Object vec3 = ServerboundInteractPacketAccessor.getFieldLocation().get(packet);
                                            hand = LivingEntityNPC.InteractionHand.fromNmsObject(ServerboundInteractPacketAccessor.getFieldHand().get(packet));
                                            location = Vector3.at(
                                                    (double) Vec3Accessor.getMethodX1().invoke(vec3),
                                                    (double) Vec3Accessor.getMethodY1().invoke(vec3),
                                                    (double) Vec3Accessor.getMethodZ1().invoke(vec3)
                                            );
                                        } else if (action.equals(ServerboundInteractPacket_i_ActionAccessor.getFieldATTACK().get(null))) {
                                            actionId = 0;
                                        }
                                    }

                                    switch (actionId) {
                                        case 0: {
                                            interactEvents.forEach(interactEvent -> interactEvent.onAttack(player, entityId));
                                            break;
                                        }
                                        case 1: {
                                            for (PlayerInteractAtEntityEvent interactEvent : interactEvents) {
                                                interactEvent.onInteract(player, hand, entityId);
                                            }
                                            break;
                                        }
                                        case 2: {
                                            for (PlayerInteractAtEntityEvent interactEvent : interactEvents) {
                                                interactEvent.onInteractAt(player, hand, location, entityId);
                                            }
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else if (ServerVersion.supports(19) && packet.getClass().equals(ServerboundChatPreviewPacketAccessor.getType())) {
                            Ruom.runAsync(() -> {
                                try {
                                    int queryId = (int) ServerboundChatPreviewPacketAccessor.getMethodQueryId1().invoke(packet);
                                    String message = (String) ServerboundChatPreviewPacketAccessor.getMethodQuery1().invoke(packet);
                                    chatPreviewEvents.forEach(chatPreviewEvent -> chatPreviewEvent.onPreviewRequest(player, queryId, message));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
