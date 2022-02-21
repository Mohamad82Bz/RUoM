package me.mohamad82.ruom.event.packet;

import org.bukkit.entity.Player;

/**
 * Listens to Clientbound and Serverbound packets.
 * Note: Run Sync if you want to use bukkit api or any other non thread-safe api.
 * It is a good practice to use methods async if you don't want to cancel the packets.
 * @since 3.1
 */
public abstract class PacketEvent {

    public PacketEvent() {
        PacketListenerManager.getInstance().register(this);
    }

    /**
     * Calls when a packet is going to be sent to a client by the server.
     * @param player The player that is going to receive the packet.
     * @param packetContainer The packet that is going to be sent to the client.
     * @return Declear that the packet should be sent or not. Return false to cancel the packet from sending.
     */
    protected abstract boolean onClientboundPacket(Player player, PacketContainer packetContainer);

    /**
     * Calls when a packet is going to be sent to the server by a client.
     * @param player The player that is sending the packet.
     * @param packetContainer The packet that the client is sending to the server.
     * @return Declear that the packet should be received or not. Return false to cancel the packet from receiving.
     */
    protected abstract boolean onServerboundPacket(Player player, PacketContainer packetContainer);

    public void unregister() {
        PacketListenerManager.getInstance().unregister(this);
    }

}
