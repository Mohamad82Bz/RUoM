package me.mohamad82.ruom.hologram;

import me.mohamad82.ruom.npc.entity.ItemNPC;
import org.bukkit.Location;
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
    public void setGlowing(boolean glowing) {
        super.setGlowing(glowing);
        if (npc != null) {
            getItemNPC().setGlowing(glowing);
        }
    }

    protected ItemNPC getItemNPC() {
        return (ItemNPC) npc;
    }

    @Override
    protected void initializeNpc(Location location) {
        npc = ItemNPC.itemNPC(location, item);
        npc.setNoGravity(true);
        npc.setGlowing(glowing);
    }

}
