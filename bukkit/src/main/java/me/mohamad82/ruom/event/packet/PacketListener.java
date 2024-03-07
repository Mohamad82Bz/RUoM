package me.mohamad82.ruom.event.packet;

import org.bukkit.entity.Player;

public interface PacketListener {

    void register();

    void unregister();

    void handle(Player player, Object packet);

}
