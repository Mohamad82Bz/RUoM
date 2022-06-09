package me.mohamad82.ruom.hologram;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityDataAccessorAccessor;
import me.mohamad82.ruom.nmsaccessors.ItemEntityAccessor;
import me.mohamad82.ruom.npc.NPC;
import me.mohamad82.ruom.npc.entity.ItemNPC;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Item3DHologramLine extends ItemHoloLine {

    private Item3DHologramLine(ItemStack item, boolean glowing, float distance) {
        super(item, glowing, distance);
    }

    public static Item3DHologramLine item3DHologramLine(ItemStack item, boolean glowing, float distance) {
        return new Item3DHologramLine(item, glowing, distance);
    }

    @Override
    public void setItem(ItemStack item) {
        super.setItem(item);
        if (npc != null) {
            getItemNPC().setItem(item);
        }
    }

    @Override
    public void setItem(ItemStack item, Player player) {
        if (npc != null) {
            Ruom.run(() -> NMSUtils.sendPacket(player, PacketUtils.getEntityDataPacket(
                    npc.getId(),
                    (int) EntityDataAccessorAccessor.getMethodGetId1().invoke(ItemEntityAccessor.getFieldDATA_ITEM().get(null)),
                    NMSUtils.getNmsItemStack(item)
            )));
        }
    }

    @Override
    public void setGlowing(boolean glowing) {
        super.setGlowing(glowing);
        if (npc != null) {
            getItemNPC().setPose(NPC.Pose.GLOWING, glowing);
        }
    }

    protected ItemNPC getItemNPC() {
        return (ItemNPC) npc;
    }

    @Override
    protected void initializeNpc(Location location) {
        npc = ItemNPC.itemNPC(location, item);
        npc.setNoGravity(true);
        npc.setPose(NPC.Pose.GLOWING, glowing);
    }

}
