package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.FireworkRocketEntityAccessor;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.OptionalInt;

public class FireworkNPC extends EntityNPC {

    private ItemStack item;

    protected FireworkNPC(Location location, Optional<ItemStack> fireworkItem) throws Exception {
        super(
                FireworkRocketEntityAccessor.getConstructor0().newInstance(NPCType.FIREWORK_ROCKET.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.FIREWORK_ROCKET
        );
        fireworkItem.ifPresent(this::setFirework);
        this.item = fireworkItem.orElse(null);
    }

    public static FireworkNPC fireworkNPC(Location location, Optional<ItemStack> fireworkItem) {
        try {
            return new FireworkNPC(location, fireworkItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setFirework(ItemStack firework) {
        this.item = firework;
        if (ServerVersion.supports(9)) {
            Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), FireworkRocketEntityAccessor.getFieldDATA_ID_FIREWORKS_ITEM().get(null), NMSUtils.getNmsItemStack(firework)));
            sendEntityData();
        } else {
            setMetadata(8, NMSUtils.getNmsItemStack(firework));
        }
    }

    public Optional<ItemStack> getFirework() {
        return Optional.ofNullable(item);
    }

    /**
     * @apiNote > 1.11
     */
    public void setAttachedEntity(OptionalInt entityId) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), FireworkRocketEntityAccessor.getFieldDATA_ATTACHED_TO_TARGET().get(null), entityId));
        sendEntityData();
    }

    /**
     * @apiNote > 1.11
     */
    public OptionalInt getAttachedEntity() {
        try {
            return (OptionalInt) SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), FireworkRocketEntityAccessor.getFieldDATA_ATTACHED_TO_TARGET().get(null));
        } catch (Exception e) {
            e.printStackTrace();
            return OptionalInt.empty();
        }
    }

    /**
     * @apiNote > 1.11
     */
    public boolean hasAttachedEntity() {
        try {
            return (boolean) FireworkRocketEntityAccessor.getMethodIsAttachedToEntity1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @apiNote > 1.14
     */
    public void setShotAtAngle(boolean shotAtAngle) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), FireworkRocketEntityAccessor.getFieldDATA_SHOT_AT_ANGLE().get(null), shotAtAngle));
        sendEntityData();
    }

    /**
     * @apiNote > 1.14
     */
    public boolean isShotAtAngle() {
        try {
            return (boolean) SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), FireworkRocketEntityAccessor.getFieldDATA_SHOT_AT_ANGLE().get(null));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @apiNote > 1.14
     */
    public boolean hasExplosion() {
        try {
            return (boolean) FireworkRocketEntityAccessor.getMethodHasExplosion1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void explode() {
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getEntityEventPacket(entity, (byte) 17));
    }

}
