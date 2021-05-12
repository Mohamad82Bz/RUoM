package me.Mohamad82.RUoM.GUI;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GUIAnimation {

    private final String animation;

    private final ItemStack firstItem;
    private final ItemStack secondItem;

    private int tick = -1;
    private int trailsLenght = 0;
    private int delayStart = 4;

    private boolean loop = false;
    private boolean trails = false;
    private boolean deTrail = false;

    private final List<Integer> slots;

    public GUIAnimation(String animation, ItemStack firstItem, ItemStack secondItem, List<Integer> slots) {
        this.animation = animation;
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        this.slots = slots;
    }

    public String getAnimation() {
        return animation;
    }

    public ItemStack getFirstItem() {
        return firstItem;
    }

    public ItemStack getSecondItem() {
        return secondItem;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getTrailsLenght() {
        return trailsLenght;
    }

    public void setTrailsLenght(int trailsLenght) {
        this.trailsLenght = trailsLenght;
    }

    public int getDelayStart() {
        return delayStart;
    }

    public void setDelayStart(int delayStart) {
        this.delayStart = delayStart;
    }

    public boolean isLoop() {
        if (loop && tick == -1) return false;
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean hasTrails() {
        return trails;
    }

    public void setTrails(boolean trails) {
        this.trails = trails;
    }

    public boolean isDeTrail() {
        return deTrail;
    }

    public void setDeTrail(boolean deTrail) {
        this.deTrail = deTrail;
    }

}
