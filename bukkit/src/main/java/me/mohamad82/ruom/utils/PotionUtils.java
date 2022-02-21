package me.mohamad82.ruom.utils;

import com.cryptomorin.xseries.XMaterial;
import me.mohamad82.ruom.nmsaccessors.CompoundTagAccessor;
import me.mohamad82.ruom.nmsaccessors.ItemStackAccessor;
import me.mohamad82.ruom.nmsaccessors.PotionUtilsAccessor;
import me.mohamad82.ruom.nmsaccessors.PotionsAccessor;
import org.bukkit.inventory.ItemStack;

public class PotionUtils {

    public static ItemStack getPotion(Potion potion) {
        try {
            Object defaultPotion = NMSUtils.getNmsItemStack(XMaterial.POTION.parseItem());
            return NMSUtils.getBukkitItemStack(PotionUtilsAccessor.getMethodSetPotion1().invoke(null, defaultPotion, PotionsAccessor.getType().getField(potion.toString().toUpperCase()).get(null)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Potion getPotion(ItemStack potion) {
        try {
            Object compoundTag = ItemStackAccessor.getMethodGetTag1().invoke(NMSUtils.getNmsItemStack(potion));
            if (compoundTag == null)
                return null;
            return Potion.valueOf(((String) CompoundTagAccessor.getMethodGetString1().invoke(compoundTag, "Potion")).substring(10).toUpperCase());
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
