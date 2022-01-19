package me.mohamad82.ruom.event.packet;

import me.mohamad82.ruom.nmsaccessors.DirectionAccessor;
import me.mohamad82.ruom.vector.Vector3;
import org.bukkit.entity.Player;

public abstract class PlayerActionEvent {

    public PlayerActionEvent() {
        PacketListenerManager.getInstance().register(this);
    }

    protected abstract void onStartDig(Player player, Vector3 blockPos, Direction direction);

    protected abstract void onStopDig(Player player, Vector3 blockPos);

    protected abstract void onDropAllItems(Player player);

    protected abstract void onDropItem(Player player);

    protected abstract void onUseItemRelease(Player player);

    protected abstract void onSwapItemsWithOffHand(Player player);

    public void unregister() {
        PacketListenerManager.getInstance().unregister(this);
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

}
