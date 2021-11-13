package me.Mohamad82.RUoM.events.packets.clientbound;

import me.Mohamad82.RUoM.events.packets.PacketContainer;
import me.Mohamad82.RUoM.events.packets.PacketEvent;
import org.bukkit.entity.Player;

/**
 * Listens to the packets that a client is receiving asynchronously.
 * @since 2.8
 */
public class AsyncClientBoundPacketEvent extends PacketEvent {

    public AsyncClientBoundPacketEvent(Player player, PacketContainer packet) {
        super(player, packet, true);
    }

}
