package me.mohamad82.ruom.itemstack.potion;

import com.cryptomorin.xseries.XMaterial;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionUtils {

    public static ItemStack getPotion(String potionId) {
        try {
            return NMSUtils.getBukkitItemStack(getNmsPotion(potionId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getNmsPotion(String potionId) {
        try {
            Object defaultPotion = NMSUtils.getNmsItemStack(XMaterial.POTION.parseItem());
            Object potion = null;
            for (Field field : PotionsAccessor.getType().getFields()) {
                Object potionObject = field.get(null);
                String potionName = (String) PotionAccessor.getFieldName().get(potionObject);
                if (potionId.equalsIgnoreCase(potionName)) {
                    potion = potionObject;
                }
            }
            if (potion == null) {
                throw new IllegalArgumentException("Potion named " + potionId + " does not exist");
            }
            return PotionUtilsAccessor.getMethodSetPotion1().invoke(null, defaultPotion, potion);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getNmsPotion(ItemStack potion) {
        return getNmsPotion(getPotionId(potion));
    }

    public static String getPotionId(ItemStack potion) {
        try {
            Object compoundTag = ItemStackAccessor.getMethodGetTag1().invoke(NMSUtils.getNmsItemStack(potion));
            if (compoundTag == null)
                return null;
            return ((String) CompoundTagAccessor.getMethodGetString1().invoke(compoundTag, "Potion")).substring(10).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Integer> getEffects(String potionId) {
        try {
            Map<String, Integer> effects = new HashMap<>();
            for (Object effectInstance : (List<Object>) PotionUtilsAccessor.getMethodGetMobEffects1().invoke(null, getNmsPotion(potionId))) {
                Object effect = MobEffectInstanceAccessor.getMethodGetEffect1().invoke(effectInstance);
                effects.put(
                        (String) MobEffectAccessor.getMethodGetDescriptionId1().invoke(effect),
                        (int) MobEffectInstanceAccessor.getMethodGetDuration1().invoke(effectInstance)
                );
            }
            return effects;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Integer> getEffects(ItemStack potion) {
        return getEffects(getPotionId(potion));
    }

}
