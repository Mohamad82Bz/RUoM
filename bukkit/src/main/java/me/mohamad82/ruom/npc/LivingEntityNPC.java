package me.mohamad82.ruom.npc;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import me.mohamad82.ruom.math.vector.Vector3;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class LivingEntityNPC extends EntityNPC {

    protected LivingEntityNPC(Object entity, Location location, NPCType npcType) {
        super(entity, location, npcType);
    }

    public void setArrowsOnBody(int arrowsOnBody) {
        Ruom.run(() -> LivingEntityAccessor.getMethodSetArrowCount1().invoke(entity, arrowsOnBody));
    }

    public int getArrowsOnBody() {
        try {
            return (int) LivingEntityAccessor.getMethodGetArrowCount1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setSleepingPos(Vector3 vector3) {
        Ruom.run(() -> LivingEntityAccessor.getMethodSetSleepingPos1().invoke(entity, BlockPosAccessor.getConstructor0().newInstance(vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ())));
    }

    @Nullable
    public Vector3 getSleepingPos() {
        try {
            Object blockPos = LivingEntityAccessor.getMethodGetSleepingPos1().invoke(entity);
            if (blockPos == null) return null;
            return Vector3.at(
                    (int) Vec3iAccessor.getMethodGetX1().invoke(blockPos),
                    (int) Vec3iAccessor.getMethodGetY1().invoke(blockPos),
                    (int) Vec3iAccessor.getMethodGetZ1().invoke(blockPos)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setEffectColor(int color) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), LivingEntityAccessor.getFieldDATA_EFFECT_COLOR_ID().get(null), color));
        sendEntityData();
    }

    public int getEffectColor() {
        try {
            return (int) SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), LivingEntityAccessor.getFieldDATA_EFFECT_COLOR_ID().get(null));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setEffectsAsAmbients(Boolean asAmbients) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), LivingEntityAccessor.getFieldDATA_EFFECT_AMBIENCE_ID().get(null), asAmbients));
        sendEntityData();
    }

    public boolean areEffectsAsAmbients() {
        try {
            return (boolean) SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), LivingEntityAccessor.getFieldDATA_EFFECT_AMBIENCE_ID().get(null));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void resetEffects() {
        Ruom.run(() -> LivingEntityAccessor.getMethodRemoveEffectParticles1().invoke(entity));
        sendEntityData();
    }

    public void setStingerCount(int stingerCount) {
        if (!ServerVersion.supports(15)) return;
        Ruom.run(() -> LivingEntityAccessor.getMethodSetStingerCount1().invoke(entity));
        sendEntityData();
    }

    public int getStingerCount() {
        try {
            return (int) LivingEntityAccessor.getMethodGetStingerCount1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void startUsingItem(InteractionHand interactionHand) {
        Ruom.run(() -> {
            LivingEntityAccessor.getFieldUseItem().set(entity, interactionHand == InteractionHand.MAIN_HAND ? equipments.get(EquipmentSlot.MAINHAND) : equipments.get(EquipmentSlot.OFFHAND));
            LivingEntityAccessor.getMethodSetLivingEntityFlag1().invoke(entity, 1, true);
            LivingEntityAccessor.getMethodSetLivingEntityFlag1().invoke(entity, 2, interactionHand == InteractionHand.OFF_HAND);
        });
        sendEntityData();
    }

    public void stopUsingItem() {
        Ruom.run(() -> LivingEntityAccessor.getMethodStopUsingItem1().invoke(entity));
        sendEntityData();
    }

    @Nullable
    public ItemStack getUseItem() {
        try {
            Object useItem = LivingEntityAccessor.getMethodGetUseItem1().invoke(entity);
            if (useItem == null || useItem.equals(NMSUtils.getNmsEmptyItemStack())) return null;
            return NMSUtils.getBukkitItemStack(useItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void collect(int collectedEntityId, int amount) {
        collect(collectedEntityId, id, amount);
    }

    public enum InteractionHand {
        MAIN_HAND(InteractionHandAccessor.getFieldMAIN_HAND()),
        OFF_HAND(InteractionHandAccessor.getFieldOFF_HAND());

        private final Object nmsObject;

        InteractionHand(Object nmsInteractionHand) {
            this.nmsObject = nmsInteractionHand;
        }

        public static InteractionHand fromNmsObject(Object nmsInteractionHand) {
            if (nmsInteractionHand.equals(InteractionHandAccessor.getFieldMAIN_HAND())) {
                return MAIN_HAND;
            } else if (nmsInteractionHand.equals(InteractionHandAccessor.getFieldOFF_HAND())) {
                return OFF_HAND;
            } else {
                return null;
            }
        }
    }

}
