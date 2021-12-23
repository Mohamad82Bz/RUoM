package me.mohamad82.ruom.hologram;

import me.mohamad82.ruom.npc.entity.ArmorStandNPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class HologramLine extends HoloLine {

    private Component component;

    private HologramLine(Component component, float distance) {
        super(distance);
        this.component = component;
    }

    public static HologramLine hologramLine(Component component, float distance) {
        return new HologramLine(component, distance);
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
        if (npc != null) {
            npc.setCustomName(component);
        }
    }

    protected ArmorStandNPC getArmorStand() {
        return (ArmorStandNPC) npc;
    }

    @Override
    protected void initializeNpc(Location location) {
        npc = ArmorStandNPC.armorStandNPC(location);
        npc.setInvisible(true);
        npc.setCustomNameVisible(true);
        npc.setCustomName(component);
        getArmorStand().setNoBasePlate(true);
        getArmorStand().setSmall(true);
        getArmorStand().setMarker(true);
    }

}
