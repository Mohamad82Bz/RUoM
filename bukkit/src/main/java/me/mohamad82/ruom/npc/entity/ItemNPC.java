package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.ItemEntityAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ItemNPC extends EntityNPC {

    protected ItemNPC(Location location, ItemStack item) throws Exception {
        super(
                ItemEntityAccessor.getConstructor0().newInstance(NMSUtils.getServerLevel(location.getWorld()), location.getX(), location.getY(), location.getZ(), NMSUtils.getNmsItemStack(item)),
                location,
                NPCType.ITEM
        );
    }

    public static ItemNPC itemNPC(Location location, ItemStack item) {
        try {
            return new ItemNPC(location, item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setItem(ItemStack item) {
        Ruom.run(() -> ItemEntityAccessor.getMethodSetItem1().invoke(entity, NMSUtils.getNmsItemStack(item)));
        sendEntityData();
    }

    public ItemStack getItem() {
        try {
            return NMSUtils.getBukkitItemStack(ItemEntityAccessor.getMethodGetItem1().invoke(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setAmount(int amount) {
        ItemStack item = getItem();
        item.setAmount(amount);
        setItem(item);
    }

    public int getAmount() {
        return getItem().getAmount();
    }

    public void collect(int collectorEntityId) {
        super.collect(id, collectorEntityId, getAmount());
    }

}
