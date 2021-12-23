package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.ArmorStandAccessor;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.Rotations;
import org.bukkit.Location;

public class ArmorStandNPC extends EntityNPC {

    private ArmorStandNPC(Location location) throws Exception {
        super(
                ArmorStandAccessor.getConstructor0().newInstance(NPCType.ARMOR_STAND.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.ARMOR_STAND
        );
        Ruom.run(() -> EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ()));
    }

    public static ArmorStandNPC armorStandNPC(Location location) {
        try {
            return new ArmorStandNPC(location);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setHeadPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetHeadPose1().invoke(entity, rotations.toNmsRotations()));
        sendEntityData();
    }

    public void setBodyPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetBodyPose1().invoke(entity, rotations.toNmsRotations()));
        sendEntityData();
    }

    public void setLeftArmPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetLeftArmPose1().invoke(entity, rotations.toNmsRotations()));
        sendEntityData();
    }

    public void setRightArmPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetRightArmPose1().invoke(entity, rotations.toNmsRotations()));
        sendEntityData();
    }

    public void setLeftLegPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetLeftLegPose1().invoke(entity, rotations.toNmsRotations()));
        sendEntityData();
    }

    public void setRightLegPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetRightLegPose1().invoke(entity, rotations.toNmsRotations()));
        sendEntityData();
    }

    public void setMarker(boolean marker) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetMarker1().invoke(entity, marker));
        sendEntityData();
    }

    public void setNoBasePlate(boolean noBasePlate) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetNoBasePlate1().invoke(entity, noBasePlate));
        sendEntityData();
    }

    public void setShowArms(boolean showArms) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetShowArms1().invoke(entity, showArms));
        sendEntityData();
    }

    public void setSmall(boolean small) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetSmall1().invoke(entity, small));
        sendEntityData();
    }

    public void setYBodyRot(float yBodyRot) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetYBodyRot1().invoke(entity, yBodyRot));
        sendEntityData();
    }

    public void setYHeadRot(float yHeadRot) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetYHeadRot1().invoke(entity, yHeadRot));
        sendEntityData();
    }

    public Rotations getHeadPose() {
        try {
            return Rotations.fromNmsRotations(ArmorStandAccessor.getMethodGetHeadPose1().invoke(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Rotations getBodyPose() {
        try {
            return Rotations.fromNmsRotations(ArmorStandAccessor.getMethodGetBodyPose1().invoke(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Rotations getLeftArmPose() {
        try {
            return Rotations.fromNmsRotations(ArmorStandAccessor.getMethodGetLeftArmPose1().invoke(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Rotations getRightArmPose() {
        try {
            return Rotations.fromNmsRotations(ArmorStandAccessor.getMethodGetRightArmPose1().invoke(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Rotations getLeftLegPose() {
        try {
            return Rotations.fromNmsRotations(ArmorStandAccessor.getMethodGetLeftLegPose1().invoke(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Rotations getRightLegPose() {
        try {
            return Rotations.fromNmsRotations(ArmorStandAccessor.getMethodGetRightLegPose1().invoke(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isMarker() {
        try {
            return (boolean) ArmorStandAccessor.getMethodIsMarker1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isNoBasePlate() {
        try {
            return (boolean) ArmorStandAccessor.getMethodIsNoBasePlate1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isShowArms() {
        try {
            return (boolean) ArmorStandAccessor.getMethodIsShowArms1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSmall() {
        try {
            return (boolean) ArmorStandAccessor.getMethodIsSmall1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
