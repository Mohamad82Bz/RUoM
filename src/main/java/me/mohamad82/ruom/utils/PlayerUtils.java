package me.mohamad82.ruom.utils;

import me.mohamad82.ruom.vector.Vector3;
import me.mohamad82.ruom.vector.Vector3Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class PlayerUtils {

    public static void sendMessage(String message, Player... players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public static boolean hasPermission(String permission, @NotNull Player... players) {
        for (Player player : players) {
            if (!player.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    public static void spawnFoodEatParticles(Location location, ItemStack foodItem) {
        final Random random = new Random();
        final Location rightSide = getRightHandLocation(location).add(0, -0.25, 0);
        for (int i = 0; i < 11; i++) {
            if (random.nextInt(7) < 1) continue;
            float a1 = (float) (random.nextInt(4) - 2) / 10;
            float a2 = (float) (random.nextInt(4) - 2) / 10;
            float a3 = (float) (random.nextInt(15) - 5) / 100;

            location.getWorld().spawnParticle(Particle.ITEM_CRACK, rightSide,
                    0, 0 + a1, 1, 0 + a2, 0.23 + a3,
                    foodItem);
        }
    }

    public static Location getRightHandLocation(Location location) {
        double yawRightHandDirection = Math.toRadians(-1 * location.getYaw());
        double x = 0.5 * Math.sin(yawRightHandDirection) + location.getX();
        double y = location.getY() + 1;
        double z = 0.5 * Math.cos(yawRightHandDirection) + location.getZ();
        return new Location(location.getWorld(), x, y, z);
    }

    public static Vector3 getPlayerVector3Location(Player player) {
        return Vector3Utils.toVector3(player.getLocation());
    }

}
