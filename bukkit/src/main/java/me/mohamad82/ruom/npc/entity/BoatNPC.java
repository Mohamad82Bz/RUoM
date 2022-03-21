package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.BoatAccessor;
import me.mohamad82.ruom.nmsaccessors.Boat_i_TypeAccessor;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.Location;

public class BoatNPC extends EntityNPC {

    protected BoatNPC(Location location) throws Exception {
        super(
                BoatAccessor.getConstructor0().newInstance(NPCType.BOAT.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.BOAT
        );
        EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ());
    }

    public static BoatNPC boatNPC(Location location) {
        try {
            return new BoatNPC(location);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BoatNPC boatNPC(Location location, Type type) {
        BoatNPC boatNPC = boatNPC(location);
        boatNPC.setBoatType(type);
        return boatNPC;
    }

    public void setDamage(float damage) {
        Ruom.run(() -> BoatAccessor.getMethodSetDamage1().invoke(entity, damage));
        sendEntityData();
    }

    public float getDamage() {
        try {
            return (float) BoatAccessor.getMethodGetDamage1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setHurtTime(int time) {
        Ruom.run(() -> BoatAccessor.getMethodSetHurtTime1().invoke(entity, time));
        sendEntityData();
    }

    public int getHurtTime() {
        try {
            return (int) BoatAccessor.getMethodGetHurtTime1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @apiNote > 1.13
     * @param time The time
     */
    public void setBubbleTime(int time) {
        Ruom.run(() -> BoatAccessor.getMethodSetBubbleTime1().invoke(entity, time));
        sendEntityData();
    }

    /**
     * @apiNote > 1.13
     * @return The time
     */
    public int getBubbleTime() {
        try {
            return (int) BoatAccessor.getMethodGetBubbleTime1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @apiNote > 1.9.4
     * @param type The type
     */
    public void setBoatType(Type type) {
        Ruom.run(() -> BoatAccessor.getMethodSetType1().invoke(entity, type.nmsObject));
        sendEntityData();
    }

    /**
     * @apiNote > 1.9.4
     * @return The type
     */
    public Type getBoatType() {
        try {
            return Type.valueOf(BoatAccessor.getMethodGetBoatType1().invoke(entity).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Type.OAK;
        }
    }

    /**
     * @apiNote > 1.13
     * @param state The state
     */
    public void setRightPaddleState(boolean state) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), BoatAccessor.getFieldDATA_ID_PADDLE_RIGHT().get(null), state));
        sendEntityData();
    }

    /**
     * @apiNote > 1.13
     * @return The state
     */
    public boolean getRightPaddleState() {
        try {
            return (boolean) SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), BoatAccessor.getFieldDATA_ID_PADDLE_RIGHT().get(null));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @apiNote > 1.13
     * @param state The state
     */
    public void setLeftPaddleState(boolean state) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), BoatAccessor.getFieldDATA_ID_PADDLE_LEFT().get(null), state));
        sendEntityData();
    }

    /**
     * @apiNote > 1.13
     * @return The state
     */
    public boolean getLeftPaddleState() {
        try {
            return (boolean) SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), BoatAccessor.getFieldDATA_ID_PADDLE_LEFT().get(null));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public enum Type {
        OAK(Boat_i_TypeAccessor.getFieldOAK()),
        SPRUCE(Boat_i_TypeAccessor.getFieldSPRUCE()),
        BIRCH(Boat_i_TypeAccessor.getFieldBIRCH()),
        JUNGLE(Boat_i_TypeAccessor.getFieldJUNGLE()),
        ACACIA(Boat_i_TypeAccessor.getFieldACACIA()),
        DARK_OAK(Boat_i_TypeAccessor.getFieldDARK_OAK());

        private final Object nmsObject;

        Type(Object nmsObject) {
            this.nmsObject = nmsObject;
        }
    }

}
