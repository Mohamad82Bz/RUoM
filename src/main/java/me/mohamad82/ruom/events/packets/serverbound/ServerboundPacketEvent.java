package me.mohamad82.ruom.events.packets.serverbound;

import me.mohamad82.ruom.events.packets.PacketContainer;
import me.mohamad82.ruom.events.packets.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Listens to the packet that is sending to a client.
 * @since 2.8
 */
public class ServerboundPacketEvent extends PacketEvent implements Cancellable {

    private boolean cancelled = false;

    public ServerboundPacketEvent(Player player, PacketContainer packet) {
        super(player, packet);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
