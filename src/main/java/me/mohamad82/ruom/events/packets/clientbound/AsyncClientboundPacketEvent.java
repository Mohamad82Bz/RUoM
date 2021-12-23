package me.mohamad82.ruom.events.packets.clientbound;

import me.mohamad82.ruom.events.packets.PacketContainer;
import me.mohamad82.ruom.events.packets.PacketEvent;
import org.bukkit.entity.Player;

/**
 * Listens to the packets that a client is receiving asynchronously.
 * @since 2.8
 */
public class AsyncClientboundPacketEvent extends PacketEvent {

    public AsyncClientboundPacketEvent(Player player, PacketContainer packet) {
        super(player, packet, true);
    }

}
