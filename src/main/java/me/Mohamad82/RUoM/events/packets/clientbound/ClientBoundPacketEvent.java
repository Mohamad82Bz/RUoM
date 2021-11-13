package me.Mohamad82.RUoM.events.packets.clientbound;

import me.Mohamad82.RUoM.events.packets.PacketContainer;
import me.Mohamad82.RUoM.events.packets.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Listens to the packets that a client is receiving.
 * @since 2.8
 */
public class ClientBoundPacketEvent extends PacketEvent implements Cancellable {

    private boolean cancelled = false;

    public ClientBoundPacketEvent(Player player, PacketContainer packet) {
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
