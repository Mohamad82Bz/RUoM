package me.mohamad82.ruom.hologram;

import org.bukkit.inventory.ItemStack;

public abstract class ItemHoloLine extends HoloLine {

    protected ItemStack item;
    protected boolean glowing;

    protected ItemHoloLine(ItemStack item, boolean glowing, float distance) {
        super(distance);
        this.item = item;
        this.glowing = glowing;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

}
