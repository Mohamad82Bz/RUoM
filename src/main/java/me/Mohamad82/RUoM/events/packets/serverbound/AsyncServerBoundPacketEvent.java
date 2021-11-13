package me.Mohamad82.RUoM.events.packets.serverbound;

import me.Mohamad82.RUoM.events.packets.PacketContainer;
import me.Mohamad82.RUoM.events.packets.PacketEvent;
import org.bukkit.entity.Player;

/**
 * Listens to the packet that is sending to a client asynchronously.
 * @since 2.8
 */
public class AsyncServerBoundPacketEvent extends PacketEvent {

    public AsyncServerBoundPacketEvent(Player player, PacketContainer packet) {
        super(player, packet, true);
    }

}
