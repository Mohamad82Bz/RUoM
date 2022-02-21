package me.mohamad82.ruom.utils;

import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PlayerUtils {

    public static void sendMessage(String message, Player... players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public static void sendActionBar(Component message, Player... players) {
        for (Player player : players) {
            NMSUtils.sendActionBar(player, message);
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

    public static int getTotalItemAmount(Player player, ItemStack item) {
        int amount = 0;
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem == null) continue;
            if (invItem.isSimilar(item)) {
                amount += invItem.getAmount();
            }
        }
        return amount;
    }

    public static boolean hasEmptySpaceFor(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() != -1) return true;
        int emptySpaces = 0;
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem == null) continue;
            if (invItem.isSimilar(item) && invItem.getAmount() != invItem.getType().getMaxStackSize()) {
                emptySpaces += invItem.getMaxStackSize() - invItem.getAmount();
            }
        }
        return emptySpaces >= item.getAmount();
    }

    public static void removeItem(Player player, ItemStack item, int amount) {
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem == null) continue;
            if (invItem.isSimilar(item)) {
                if (amount > invItem.getAmount()) {
                    amount -= invItem.getAmount();
                    invItem.setAmount(0);
                } else {
                    invItem.setAmount(invItem.getAmount() - amount);
                    amount = 0;
                }
            }
            if (amount == 0) break;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean hasItemInMainHand(Player player, Material material) {
        if (ServerVersion.supports(9)) {
            return player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() == material;
        } else {
            return player.getInventory().getItemInHand() != null && player.getInventory().getItemInHand().getType() == material;
        }
    }

    public static boolean hasItemInOffHand(Player player, Material material) {
        if (!ServerVersion.supports(9)) return false;
        return player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == material;
    }

    public static boolean hasItemInHand(Player player, Material material) {
        boolean hasInMainHand = hasItemInMainHand(player, material);
        if (ServerVersion.supports(9)) {
            boolean hasInOffHand = hasItemInOffHand(player, material);
            if (!hasInOffHand)
                return hasInMainHand;
            else
                return hasInOffHand;
        } else {
            return hasInMainHand;
        }
    }

    @Nullable
    public static ItemStack getInteractableItemInHand(Player player, Material material) {
        if (ServerVersion.supports(9)) {
            if (!hasItemInHand(player, material)) return null;
            Material handMaterial = player.getInventory().getItemInMainHand() != null ? player.getInventory().getItemInMainHand().getType() : null;
            Material offHandMaterial = player.getInventory().getItemInOffHand() != null ? player.getInventory().getItemInOffHand().getType() : null;
            boolean hasInOffHand = offHandMaterial == material;

            if (hasInOffHand) {
                if (handMaterial != null && handMaterial.isInteractable()) return player.getInventory().getItemInMainHand();
                else return player.getInventory().getItemInOffHand();
            } else {
                return player.getInventory().getItemInMainHand();
            }
        } else {
            return hasItemInMainHand(player, material) ? player.getInventory().getItemInHand() : null;
        }
    }

    public static void spawnFoodEatParticles(Location location, Material foodMaterial) {
        final Random random = new Random();
        final Location rightSide = getRightHandLocation(location).add(0, -0.25, 0);
        for (int i = 0; i < 11; i++) {
            if (random.nextInt(7) < 1) continue;
            float a1 = (float) (random.nextInt(4) - 2) / 10;
            float a2 = (float) (random.nextInt(4) - 2) / 10;
            float a3 = (float) (random.nextInt(15) - 5) / 100;

            location.getWorld().spawnParticle(Particle.ITEM_CRACK, rightSide,
                    0, 0 + a1, 1, 0 + a2, 0.23 + a3,
                    new ItemStack(foodMaterial));
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
        return Vector3UtilsBukkit.toVector3(player.getLocation());
    }

}
