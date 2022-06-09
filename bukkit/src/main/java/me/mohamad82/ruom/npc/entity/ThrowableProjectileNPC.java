package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.nmsaccessors.ThrowableItemProjectileAccessor;
import me.mohamad82.ruom.nmsaccessors.ThrownPotionAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ThrowableProjectileNPC extends EntityNPC {

    protected ThrowableProjectileNPC(Location location, ItemStack item) throws Exception {
        super(
                ServerVersion.supports(14) ? ThrownPotionAccessor.getConstructor0().newInstance(NPCType.POTION.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())) :
                        ThrownPotionAccessor.getConstructor1().newInstance(NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.POTION
        );
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

    /**
     * TODO I don't know how to do this in 1.8
     * @apiNote > 1.9
     */
    public void setItem(ItemStack item) {
        if (!ServerVersion.supports(9)) return;
        Object nmsItem = NMSUtils.getNmsItemStack(item);
        if (ServerVersion.supports(16)) {
            Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), ThrowableItemProjectileAccessor.getFieldDATA_ITEM_STACK().get(null), nmsItem));
        } else {
            Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), ThrownPotionAccessor.getFieldDATA_ITEM_STACK().get(null), nmsItem));
        }
        sendEntityData();
    }

    /**
     * @apiNote > 1.9
     */
    public ItemStack getItem() {
        try {
            return NMSUtils.getBukkitItemStack((ServerVersion.supports(16) ? ThrowableItemProjectileAccessor.getMethodGetItemRaw1().invoke(entity) :
                    SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), ThrownPotionAccessor.getFieldDATA_ITEM_STACK().get(null))));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
