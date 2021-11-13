package me.Mohamad82.RUoM.events.packets;

import com.cryptomorin.xseries.ReflectionUtils;

public class PacketContainer {

    private final Object packet;
    private final String name;

    protected PacketContainer(Object packet) {
        this.packet = packet;

        try {
            String[] rawNameSplit = packet.toString().split("\\.");
            String[] rawNameSplit2 = rawNameSplit[rawNameSplit.length - 1].split("@");
            this.name = rawNameSplit2[0];

            if (ReflectionUtils.getNMSClass("server.protocol.game", name) == null) {
                throw new IllegalArgumentException("Given instance is not a packet.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Given object is not a packet instance.");
        }
    }

    /**
     * Returns the packet object.
     * @return The packet object
     */
    public Object getPacket() {
        return packet;
    }

    /**
     * Returns the name of the packet.
     * Example of returning string: "PacketPlayOutEntityMetadata"
     * @return The packet name
     */
    public String getName() {
        return name;
    }

}
