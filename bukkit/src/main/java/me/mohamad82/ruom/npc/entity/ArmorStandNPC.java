package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.ArmorStandAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.Rotations;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.Location;

public class ArmorStandNPC extends EntityNPC {

    private Rotations headPose, bodyPose, leftArmPose, rightArmPose, leftLegPose, rightLegPose;
    private boolean marker, small, noBasePlate, showArms;
    private float yBodyRot, yHeadRot;

    protected ArmorStandNPC(Location location) throws Exception {
        super(
                ArmorStandAccessor.getConstructor0().newInstance(NPCType.ARMOR_STAND.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.ARMOR_STAND
        );
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
        headPose = rotations;
        sendEntityData();
    }

    public void setBodyPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetBodyPose1().invoke(entity, rotations.toNmsRotations()));
        bodyPose = rotations;
        sendEntityData();
    }

    public void setLeftArmPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetLeftArmPose1().invoke(entity, rotations.toNmsRotations()));
        leftArmPose = rotations;
        sendEntityData();
    }

    public void setRightArmPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetRightArmPose1().invoke(entity, rotations.toNmsRotations()));
        rightArmPose = rotations;
        sendEntityData();
    }

    public void setLeftLegPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetLeftLegPose1().invoke(entity, rotations.toNmsRotations()));
        leftLegPose = rotations;
        sendEntityData();
    }

    public void setRightLegPose(Rotations rotations) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetRightLegPose1().invoke(entity, rotations.toNmsRotations()));
        rightLegPose = rotations;
        sendEntityData();
    }

    @Override
    public void setNoGravity(boolean noGravity) {
        if (ServerVersion.supports(10)) {
            super.setNoGravity(noGravity);
        } else {
            Ruom.run(() -> ArmorStandAccessor.getMethodSetGravity1().invoke(entity, !noGravity));
        }
    }

    @Override
    public boolean isNoGravity() {
        if (ServerVersion.supports(10)) {
            return super.isNoGravity();
        } else {
            try {
                return !(boolean) ArmorStandAccessor.getMethodHasGravity1().invoke(entity);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public void setMarker(boolean marker) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetMarker1().invoke(entity, marker));
        this.marker = marker;
        sendEntityData();
    }

    public void setNoBasePlate(boolean noBasePlate) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetNoBasePlate1().invoke(entity, noBasePlate));
        this.noBasePlate = noBasePlate;
        sendEntityData();
    }

    public void setShowArms(boolean showArms) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetShowArms1().invoke(entity, showArms));
        this.showArms = showArms;
        sendEntityData();
    }

    public void setSmall(boolean small) {
        Ruom.run(() -> ArmorStandAccessor.getMethodSetSmall1().invoke(entity, small));
        this.small = small;
        sendEntityData();
    }

    /**
     * @apiNote > 1.11
     */
    public void setYBodyRot(float yBodyRot) {
        if (!ServerVersion.supports(11)) return;
        Ruom.run(() -> ArmorStandAccessor.getMethodSetYBodyRot1().invoke(entity, yBodyRot));
        this.yBodyRot = yBodyRot;
        sendEntityData();
    }

    /**
     * @apiNote > 1.11
     */
    public void setYHeadRot(float yHeadRot) {
        if (!ServerVersion.supports(11)) return;
        Ruom.run(() -> ArmorStandAccessor.getMethodSetYHeadRot1().invoke(entity, yHeadRot));
        this.yHeadRot = yHeadRot;
        sendEntityData();
    }

    public Rotations getHeadPose() {
        return headPose;
    }

    public Rotations getBodyPose() {
        return bodyPose;
    }

    public Rotations getLeftArmPose() {
        return leftArmPose;
    }

    public Rotations getRightArmPose() {
        return rightArmPose;
    }

    public Rotations getLeftLegPose() {
        return leftLegPose;
    }

    public Rotations getRightLegPose() {
        return rightLegPose;
    }

    public boolean isMarker() {
        return marker;
    }

    public boolean isNoBasePlate() {
        return noBasePlate;
    }

    public boolean isShowArms() {
        return showArms;
    }

    public boolean isSmall() {
        return small;
    }

    public float getyBodyRot() {
        return yBodyRot;
    }

    public float getyHeadRot() {
        return yHeadRot;
    }

}
