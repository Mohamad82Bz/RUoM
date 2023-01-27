package me.mohamad82.ruom.utils;

import com.cryptomorin.xseries.XMaterial;
import kotlin.Pair;
import me.mohamad82.ruom.adventure.AdventureApi;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.texture.ItemTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlayerUtils {

    public static void sendMessage(String message, Player... players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public static void sendActionBar(Component message, Player... players) {
        for (Player player : players) {
            AdventureApi.get().player(player).sendActionBar(message);
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
        final Location rightArm = getRightHandLocation(location).add(0, -0.25, 0);
        ParticleBuilder particleBuilder = new ParticleBuilder(ParticleEffect.ITEM_CRACK, rightArm).setParticleData(new ItemTexture(new ItemStack(foodMaterial)));
        for (int i = 0; i < 11; i++) {
            if (random.nextInt(7) < 1) continue;
            float a1 = (float) (random.nextInt(4) - 2) / 10;
            float a2 = (float) (random.nextInt(4) - 2) / 10;
            float a3 = (float) (random.nextInt(15) - 5) / 100;

            particleBuilder.setAmount(1);
            particleBuilder.setOffset(a1, 1, a2);
            particleBuilder.setSpeed((float) 0.23 + a3);
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

    public static void teleport(Player player, Vector3 location) {
        player.teleport(Vector3UtilsBukkit.toLocation(player.getWorld(), location));
    }

    /**
     * Hides inventory content with optional fake items.
     * @param player The player
     * @param fakeItems Optional fake items. Note: Fake items are packet based and will be removed when updating inventory (Like on inventory click)
     * @throws IllegalArgumentException If fake item slot is below 0 or greater than 45
     */
    @SafeVarargs
    public static void hideInventoryContent(Player player, Pair<Integer, ItemStack>... fakeItems) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(XMaterial.AIR.parseItem());
        }
        items.add(player.getInventory().getItem(EquipmentSlot.HEAD));
        items.add(player.getInventory().getItem(EquipmentSlot.CHEST));
        items.add(player.getInventory().getItem(EquipmentSlot.LEGS));
        items.add(player.getInventory().getItem(EquipmentSlot.FEET));
        for (int i = 9; i < 45; i++) {
            items.add(XMaterial.AIR.parseItem());
        }
        for (Pair<Integer, ItemStack> fakeItem : fakeItems) {
            if (fakeItem.getFirst() < 0 || fakeItem.getFirst() > 45) {
                throw new IllegalArgumentException("Tried to set fake items on slots other than 0 to 45.");
            }
            items.set(fakeItem.getFirst(), fakeItem.getSecond());
        }
        NMSUtils.sendPacketSync(player, PacketUtils.getContainerSetContentPacket(0, 0, items, XMaterial.AIR.parseItem()));
    }

    /**
     * Hides inventory content with fake items.
     * @param player The player
     * @param fakeItems Fake items. Note: Fake items are packet based and will be removed when updating inventory (Like on inventory click)
     * @throws IllegalArgumentException If fake item slot is below 0 or greater than 45
     */
    public static void hideInventoryContent(Player player, Map<Integer, ItemStack> fakeItems) {
        Pair<Integer, ItemStack>[] fakeItemsArray = new Pair[fakeItems.size()];
        int i = 0;
        for (Map.Entry<Integer, ItemStack> entry : fakeItems.entrySet()) {
            fakeItemsArray[i] = new Pair<>(entry.getKey(), entry.getValue());
            i++;
        }
        hideInventoryContent(player, fakeItemsArray);
    }
}
