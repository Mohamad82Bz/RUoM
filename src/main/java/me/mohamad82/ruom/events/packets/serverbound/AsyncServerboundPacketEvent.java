package me.mohamad82.ruom.events.packets.serverbound;

import me.mohamad82.ruom.events.packets.PacketContainer;
import me.mohamad82.ruom.events.packets.PacketEvent;
import org.bukkit.entity.Player;

/**
 * Listens to the packet that is sending to a client asynchronously.
 * @since 2.8
 */
public class AsyncServerboundPacketEvent extends PacketEvent {

    public AsyncServerboundPacketEvent(Player player, PacketContainer packet) {
        super(player, packet, true);
    }

}
