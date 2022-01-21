package me.mohamad82.ruom.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;

public class IPlayer implements Audience {

    private final Player player;

    private IPlayer(Player player) {
        this.player = player;
    }

    public static IPlayer wrap(Player player) {
        return new IPlayer(player);
    }

    public Player get() {
        return player;
    }

    public void sendActionBar(Component message) {
        NMSUtils.sendActionBar(player, message);
    }

    public Location getRightHandLocation() {
        return PlayerUtils.getRightHandLocation(player.getLocation());
    }

    public CompletableFuture<Void> sendPacket(Object... packets) {
        return NMSUtils.sendPacket(player, packets);
    }

    public void sendPacketSync(Object... packets) {
        NMSUtils.sendPacketSync(player, packets);
    }

    public Object getNmsPlayer() {
        return NMSUtils.getServerPlayer(player);
    }

    public void disconnect(Component message) {
        NMSUtils.disconnect(player, message);
    }

}
