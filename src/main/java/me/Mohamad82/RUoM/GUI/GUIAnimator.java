package me.Mohamad82.RUoM.GUI;

import me.Mohamad82.RUoM.GUI.Exceptions.AnimatorNullPluginException;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GUIAnimator {

    private final JavaPlugin plugin;
    private boolean isCanceled = false;

    public GUIAnimator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void animate(GUIAnimation guiAnimation, Inventory gui, int totalAnimateTime, List<Integer> prevents) {
        if (plugin == null) {
            throw new AnimatorNullPluginException();
        }
        ItemStack firstItem = guiAnimation.getFirstItem();
        ItemStack secondItem = guiAnimation.getSecondItem();
        List<Integer> slots = guiAnimation.getSlots();

        if (prevents == null) prevents = new ArrayList<>();
        List<Integer> finalPrevents = prevents;

        for (int slot : slots) {
            if (!finalPrevents.contains(slot))
                gui.setItem(slot, firstItem);
        }

        int tick = guiAnimation.getTick();
        if (tick == -1) {
            if (totalAnimateTime != 0)
                tick = totalAnimateTime / (guiAnimation.getSlots().size() + guiAnimation.getTrailsLenght());
            else tick = 1;
        }

        new BukkitRunnable() {
            int i = 0;
            int j = 0;
            final boolean isTrailsOn = guiAnimation.hasTrails();
            final boolean isLoopOn = guiAnimation.isLoop();
            final boolean deTrail = guiAnimation.isDeTrail();
            final int trailsLenght = guiAnimation.getTrailsLenght();
            int trail = 0;

            public void run() {
                if (isCanceled) {
                    cancel();
                    return;
                }
                try {
                    int slot = slots.get(i);
                    if (!(finalPrevents.contains(slot))) {
                        gui.setItem(slot, secondItem);
                    }
                } catch (IndexOutOfBoundsException e) {
                    if (!deTrail && !isLoopOn) {
                        cancel();
                        return;
                    }
                }
                if (isTrailsOn) {
                    trail++;
                    if (trail > trailsLenght) {
                        int slot = slots.get(i - trailsLenght);
                        if (!(finalPrevents.contains(slot))) {
                            gui.setItem(slot, firstItem);
                        }
                    }
                }
                i++;
                if (slots.size() <= i) {
                    if (isTrailsOn) {
                        if (slots.size() <= (i - trailsLenght)) {
                            if (isLoopOn) {
                                int slot = slots.get(j);
                                if (!(finalPrevents.contains(slot)))
                                    gui.setItem(slot, secondItem);
                                i = j + 1;
                                trail = j + 1;
                                j = 0;
                            } else cancel();
                        }
                        if (slots.size() <= i - 1) {
                            if (isLoopOn) {
                                int slot = slots.get(j);
                                if (!(finalPrevents.contains(slot)))
                                    gui.setItem(slot, secondItem);
                                j++;
                            }
                        }
                    } else {
                        if (isLoopOn) {
                            i = 0;
                            trail = 0;
                        } else cancel();
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin,
                guiAnimation.getDelayStart(), tick);
    }

    public void animate(List<GUIAnimation> guiAnimations, Inventory gui, int totalAnimateTime, List<Integer> prevents) {
        for (GUIAnimation guiAnimation : guiAnimations) {
            this.animate(guiAnimation, gui, totalAnimateTime, prevents);
        }
    }

    public void animate(List<GUIAnimation> guiAnimations, Inventory gui, int totalAnimateTime) {
        for (GUIAnimation guiAnimation : guiAnimations) {
            this.animate(guiAnimation, gui, totalAnimateTime, null);
        }
    }

    public void animate(List<GUIAnimation> guiAnimations, Inventory gui) {
        for (GUIAnimation guiAnimation : guiAnimations) {
            this.animate(guiAnimation, gui, 0, null);
        }
    }

    public void cancel() {
        this.isCanceled = true;
    }

}
