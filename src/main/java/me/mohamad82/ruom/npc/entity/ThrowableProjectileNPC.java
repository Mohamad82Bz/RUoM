package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.ThrowableItemProjectileAccessor;
import me.mohamad82.ruom.nmsaccessors.ThrownPotionAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ThrowableProjectileNPC extends EntityNPC {

    private ThrowableProjectileNPC(Location location, ItemStack item) throws Exception {
        super(
                ThrownPotionAccessor.getConstructor0().newInstance(NPCType.POTION.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.POTION
        );
        Ruom.run(() -> EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ()));
        setItem(item);
    }

    public static ThrowableProjectileNPC throwableProjectileNPC(Location location, ItemStack item) {
        try {
            return new ThrowableProjectileNPC(location, item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setItem(ItemStack item) {
        Ruom.run(() -> ThrowableItemProjectileAccessor.getMethodSetItem1().invoke(entity, NMSUtils.getNmsItemStack(item)));
        sendEntityData();
    }

    public ItemStack getItem() {
        try {
            return NMSUtils.getBukkitItemStack(ThrowableItemProjectileAccessor.getMethodGetItemRaw1().invoke(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
