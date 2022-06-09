package me.mohamad82.ruom.hologram;

import com.google.common.collect.ImmutableList;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.npc.NPC;
import me.mohamad82.ruom.npc.entity.ArmorStandNPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class AnimatedHologramLine extends HoloLine {

    private final ImmutableList<Component> components;
    private final int refresh;
    private BukkitTask refreshTask;

    private AnimatedHologramLine(List<Component> components, int refresh, float distance) {
        super(distance);
        if (components.size() <= 1) {
            throw new IllegalStateException("AnimatedHologramLine must have at least two component in its list.");
        }
        this.components = ImmutableList.copyOf(components);
        this.refresh = refresh;
    }

    public static AnimatedHologramLine animatedHologramLine(List<Component> components, int refresh, float distance) {
        return new AnimatedHologramLine(components, refresh, distance);
    }

    public List<Component> getComponents() {
        return components;
    }

    public int getRefresh() {
        return refresh;
    }

    public ArmorStandNPC getArmorStand() {
        return (ArmorStandNPC) npc;
    }

    public void setArmorStand(ArmorStandNPC armorStandNPC) {
        this.npc = armorStandNPC;
    }

    public void cancelTask() {
        if (refreshTask != null) {
            refreshTask.cancel();
        }
    }

    @Override
    protected void initializeNpc(Location location) {
        npc = ArmorStandNPC.armorStandNPC(location);
        npc.setPose(NPC.Pose.INVISIBLE, true);
        npc.setCustomNameVisible(true);
        npc.setCustomName(components.get(0));
        getArmorStand().setNoBasePlate(true);
        getArmorStand().setSmall(true);
        getArmorStand().setMarker(true);

        refreshTask = Ruom.runAsync(new Runnable() {
            int displayedLine = 1;
            public void run() {
                npc.setCustomName(components.get(displayedLine));
                displayedLine++;
                if (displayedLine >= components.size()) {
                    displayedLine = 0;
                }
            }
        }, refresh, refresh);
    }

}
