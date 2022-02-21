package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.ArrowAccessor;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.math.vector.Vector3;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ArrowNPC extends EntityNPC {

    protected ArrowNPC(Location location) throws Exception {
        super(
                ArrowAccessor.getConstructor0().newInstance(NPCType.ARROW.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.ARROW
        );
        EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ());
    }

    public static ArrowNPC arrowNPC(Location location) {
        try {
            return new ArrowNPC(location);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setEffectsFromItem(ItemStack item) {
        Ruom.run(() -> ArrowAccessor.getMethodSetEffectsFromItem1().invoke(entity, NMSUtils.getNmsItemStack(item)));
        sendEntityData();
    }

    public void makeParticles(Vector3 location) {
        Ruom.run(() -> {
            EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ());
            ArrowAccessor.getMethodMakeParticle1().invoke(entity);
        });
    }

    public void setColor(int color) {
        Ruom.run(() -> ArrowAccessor.getMethodSetFixedColor1().invoke(entity, color));
        sendEntityData();
    }

    public int getColor() {
        try {
            return (int) ArrowAccessor.getMethodGetColor1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void collect(int collectorEntityId) {
        super.collect(id, collectorEntityId, 1);
    }

}
