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

    public Object connect(InetSocketAddress inetSocketAddress, boolean flag) {
        return NMSUtils.connectToServer(player, inetSocketAddress, flag);
    }

    public Object connectLocal(SocketAddress socketAddress) {
        return NMSUtils.connectToLocalServer(player, socketAddress);
    }

    public void disconnect(Component message) {
        NMSUtils.disconnect(player, message);
    }

}
