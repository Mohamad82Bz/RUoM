package me.mohamad82.ruom.utils.item;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.CrossbowItemAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CrossbowUtils {

    public static boolean isCharged(ItemStack item) {
        validate(item);
        try {
            return (boolean) CrossbowItemAccessor.getMethodIsCharged1().invoke(null, NMSUtils.getNmsItemStack(item));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setCharged(ItemStack item, boolean charged) {
        validate(item);
        Ruom.run(() -> CrossbowItemAccessor.getMethodSetCharged1().invoke(null, NMSUtils.getNmsItemStack(item), charged));
    }

    public static List<ItemStack> getChargedProjectiles(ItemStack item) {
        validate(item);
        try {
            List<Object> nmsProjectiles = (List<Object>) CrossbowItemAccessor.getMethodGetChargedProjectiles1().invoke(null, NMSUtils.getNmsItemStack(item));
            List<ItemStack> projectiles = new ArrayList<>();
            for (Object nmsItem : nmsProjectiles) {
                projectiles.add(NMSUtils.getBukkitItemStack(nmsItem));
            }
            return projectiles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clearChargedProjectiles(ItemStack item) {
        validate(item);
        Ruom.run(() -> CrossbowItemAccessor.getMethodClearChargedProjectiles1().invoke(null, NMSUtils.getNmsItemStack(item)));
    }

    public static int getNeededChargeDuration(ItemStack item) {
        validate(item);
        try {
            return (int) CrossbowItemAccessor.getMethodGetChargeDuration1().invoke(null, NMSUtils.getNmsItemStack(item));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static float getPowerForTime(int i, ItemStack item) {
        try {
            return Math.min((float) i / getNeededChargeDuration(item), 1);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Sound getLoadingStartSound(int quickChargeLevel) {
        switch (quickChargeLevel) {
            case 1: return XSound.ITEM_CROSSBOW_QUICK_CHARGE_1.parseSound();
            case 2: return XSound.ITEM_CROSSBOW_QUICK_CHARGE_2.parseSound();
            case 3: return XSound.ITEM_CROSSBOW_QUICK_CHARGE_3.parseSound();
            default: return XSound.ITEM_CROSSBOW_LOADING_START.parseSound();
        }
    }

    public Sound getLoadingMiddleSound() {
        return XSound.ITEM_CROSSBOW_LOADING_MIDDLE.parseSound();
    }

    public Sound getLoadingEndSound() {
        return XSound.ITEM_CROSSBOW_LOADING_END.parseSound();
    }

    private static void validate(ItemStack item) {
        if (!ServerVersion.supports(14))
            throw new IllegalStateException("Crossbow isn't available in versions below 1.14");
        if (item.getType() != XMaterial.CROSSBOW.parseMaterial())
            throw new IllegalArgumentException("Given itemstack is not a crossbow.");
    }

}
