package me.mohamad82.ruom.hologram;

import me.mohamad82.ruom.npc.NPC;
import org.bukkit.Location;

public abstract class HoloLine {

    private final float distance;
    protected NPC npc;

    protected HoloLine(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    protected NPC getNpc() {
        return npc;
    }

    protected abstract void initializeNpc(Location location);

}
