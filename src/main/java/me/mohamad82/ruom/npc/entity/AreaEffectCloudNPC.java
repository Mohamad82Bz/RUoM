package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.AreaEffectCloudAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.Location;
import org.bukkit.Particle;

public class AreaEffectCloudNPC extends EntityNPC {

    protected AreaEffectCloudNPC(Location location) throws Exception {
        super(
                AreaEffectCloudAccessor.getConstructor0().newInstance(NMSUtils.getServerLevel(location.getWorld()), location.getX(), location.getY(), location.getZ()),
                location,
                NPCType.AREA_EFFECT_CLOUD
        );
    }

    public static AreaEffectCloudNPC areaEffectCloudNPC(Location location) {
        try {
            return new AreaEffectCloudNPC(location);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setRadius(float radius) {
        Ruom.run(() -> AreaEffectCloudAccessor.getMethodSetRadius1().invoke(entity, radius));
        sendEntityData();
    }

    public float getRadius() {
        try {
            return (float) AreaEffectCloudAccessor.getMethodGetRadius1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setColor(int color) {
        Ruom.run(() -> AreaEffectCloudAccessor.getMethodSetFixedColor1().invoke(entity, color));
        sendEntityData();
    }

    public int getColor() {
        try {
            return (int) AreaEffectCloudAccessor.getMethodGetColor1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setParticle(Particle particle) {
        Ruom.run(() -> AreaEffectCloudAccessor.getMethodSetParticle1().invoke(entity, NMSUtils.getParticleOptions(particle)));
        sendEntityData();
    }

    public Particle getParticle() {
        try {
            return NMSUtils.getBukkitParticle(AreaEffectCloudAccessor.getMethodGetParticle1().invoke(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
