package me.mohamad82.ruom.event.packet;

import me.mohamad82.ruom.nmsaccessors.DirectionAccessor;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.nmsaccessors.ServerboundPlayerActionPacketAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerboundPlayerActionPacket_i_ActionAccessor;
import me.mohamad82.ruom.nmsaccessors.Vec3iAccessor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class PlayerActionEvent implements PacketListener {

    public static final Set<PlayerActionEvent> HANDLER_LIST = new HashSet<>();

    public PlayerActionEvent() {
        register();
    }

    protected abstract void onStartDig(Player player, Vector3 blockPos, Direction direction);

    protected abstract void onStopDig(Player player, Vector3 blockPos);

    protected abstract void onDropAllItems(Player player);

    protected abstract void onDropItem(Player player);

    protected abstract void onUseItemRelease(Player player);

    protected abstract void onSwapItemsWithOffHand(Player player);

    public void register() {
        HANDLER_LIST.add(this);
    }

    public void unregister() {
        HANDLER_LIST.remove(this);
    }

    public enum Direction {
        DOWN(DirectionAccessor.getFieldDOWN()),
        UP(DirectionAccessor.getFieldUP()),
        NORTH(DirectionAccessor.getFieldNORTH()),
        SOUTH(DirectionAccessor.getFieldSOUTH()),
        WEST(DirectionAccessor.getFieldWEST()),
        EAST(DirectionAccessor.getFieldEAST());

        private final Object nmsObject;

        Direction(Object nmsObject) {
            this.nmsObject = nmsObject;
        }
    }

    public void handle(Player player, Object packet) {
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
                onStartDig(player, blockPos, direction);
            } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldSTOP_DESTROY_BLOCK()) || action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldABORT_DESTROY_BLOCK())) {
                onStopDig(player, blockPos);
            } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldDROP_ALL_ITEMS())) {
                onDropAllItems(player);
            } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldDROP_ITEM())) {
                onDropItem(player);
            } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldRELEASE_USE_ITEM())) {
                onUseItemRelease(player);
            } else if (action.equals(ServerboundPlayerActionPacket_i_ActionAccessor.getFieldSWAP_ITEM_WITH_OFFHAND())) {
                onSwapItemsWithOffHand(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
