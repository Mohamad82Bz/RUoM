package me.Mohamad82.RUoM.events.packets;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final PacketContainer packet;

    public PacketEvent(Player player, PacketContainer packet) {
        this(player, packet, false);
    }

    public PacketEvent(Player player, PacketContainer packet, boolean isAsync) {
        super(isAsync);
        this.player = player;
        this.packet = packet;

        PacketListenerManager.initialize();
    }

    public Player getPlayer() {
        return player;
    }

    public PacketContainer getPacket() {
        return packet;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
