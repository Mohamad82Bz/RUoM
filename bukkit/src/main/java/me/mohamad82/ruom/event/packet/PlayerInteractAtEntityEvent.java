package me.mohamad82.ruom.event.packet;

import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.npc.LivingEntityNPC;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * A packet-based event that triggers whenever a player interact on both server-sided and client-sided (sent by packets) entities.
 * Usable for NPCs.
 */
public abstract class PlayerInteractAtEntityEvent implements PacketListener {

    public static final Set<PlayerInteractAtEntityEvent> HANDLER_LIST = new HashSet<>();

    public PlayerInteractAtEntityEvent() {
        register();
    }

    protected abstract void onInteract(Player player, LivingEntityNPC.InteractionHand hand, int entityId);

    protected abstract void onInteractAt(Player player, LivingEntityNPC.InteractionHand hand, Vector3 location, int entityId);

    protected abstract void onAttack(Player player, int entityId);

    public void register() {
        HANDLER_LIST.add(this);
    }

    public void unregister() {
        HANDLER_LIST.remove(this);
    }

    public void handle(Player player, Object packet) {
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
                    onAttack(player, entityId);
                    break;
                }
                case 1: {
                    onInteract(player, hand, entityId);
                    break;
                }
                case 2: {
                    onInteractAt(player, hand, location, entityId);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
