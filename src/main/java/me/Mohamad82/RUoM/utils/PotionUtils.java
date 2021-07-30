package me.Mohamad82.RUoM.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;

public class PotionUtils {

    private final static Class<?> POTIONS, CRAFT_ITEMSTACK, POTION_UTIL, POTION_REGISTRY, ITEMSTACK, NBT_TAG_COMPOUND;

    static {
        POTIONS = ReflectionUtils.getNMSClass("world.item.alchemy", "Potions");
        CRAFT_ITEMSTACK = ReflectionUtils.getCraftClass("inventory.CraftItemStack");
        POTION_UTIL = ReflectionUtils.getNMSClass("world.item.alchemy", "PotionUtil");
        POTION_REGISTRY = ReflectionUtils.getNMSClass("world.item.alchemy", "PotionRegistry");
        ITEMSTACK = ReflectionUtils.getNMSClass("world.item", "ItemStack");
        NBT_TAG_COMPOUND = ReflectionUtils.getNMSClass("nbt", "NBTTagCompound");
    }

    public static ItemStack getPotion(String name) {
        try {
            Object potionRegistry = POTIONS.getField(name.toUpperCase()).get(null);
            ItemStack defaultPotion = XMaterial.POTION.parseItem();

            Object nmsItem = CRAFT_ITEMSTACK.getMethod("asNMSCopy", ItemStack.class)
                    .invoke(null, defaultPotion);
            Object nmsPotion = POTION_UTIL.getMethod("a", ITEMSTACK, POTION_REGISTRY)
                    .invoke(null, nmsItem, potionRegistry);

            return (ItemStack) CRAFT_ITEMSTACK.getMethod("asBukkitCopy", ITEMSTACK)
                    .invoke(null, nmsPotion);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Potion getPotion(ItemStack potion) {
        try {
            Object nmsItem = CRAFT_ITEMSTACK.getMethod("asNMSCopy", ItemStack.class)
                    .invoke(null, potion);
            Object nbtTagCompound = ITEMSTACK.getMethod("getTag").invoke(nmsItem);

            if (nbtTagCompound == null) return null;

            return Potion.valueOf(((String) NBT_TAG_COMPOUND.getMethod("getString", String.class)
                    .invoke(nbtTagCompound, "Potion")).substring(10).toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum Potion {
        EMPTY,
        WATER,
        MUNDANE,
        THICK,
        AWKWARD,
        NIGHT_VISION,
        LONG_NIGHT_VISION,
        INVISIBILITY,
        LONG_INVISIBILITY,
        LEAPING,
        LONG_LEAPING,
        STRONG_LEEPING,
        FIRE_RESISTANCE,
        LONG_FIRE_RESISTANCE,
        SWIFTNESS,
        LONG_SWIFTNESS,
        STRONG_SWIFTNESS,
        SLOWNESS,
        LONG_SLOWNESS,
        STRONG_SLOWNESS,
        TURTLE_MASTER,
        LONG_TURTLE_MASTER,
        STRONG_TURTLE_MASTER,
        WATER_BREATHING,
        LONG_WATER_BREATHING,
        HEALING,
        STRONG_HEALING,
        HARMING,
        STRONG_HARMING,
        POISON,
        LONG_POISON,
        STRONG_POISON,
        REGENERATION,
        LONG_REGENERATION,
        STRONG_REGENERATION,
        STRENGTH,
        LONG_STRENGTH,
        STRONG_STRENGTH,
        WEAKNESS,
        LONG_WEAKNESS,
        LUCK,
        SLOW_FALLING,
        LONG_SLOW_FALLING
    }

}
