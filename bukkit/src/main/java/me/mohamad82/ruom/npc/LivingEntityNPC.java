package me.mohamad82.ruom.npc;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.metadata.livingentity.V1_8_LivingEntityMeta;
import me.mohamad82.ruom.nmsaccessors.InteractionHandAccessor;
import me.mohamad82.ruom.nmsaccessors.LivingEntityAccessor;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class LivingEntityNPC extends EntityNPC {

    private int effectColor = 0;
    private boolean effectAsAmbient = false;

    protected LivingEntityNPC(Object entity, Location location, NPCType npcType) {
        super(entity, location, npcType);
    }

    public void setBodyArrows(int arrowsOnBody) {
        Ruom.run(() -> LivingEntityAccessor.getMethodSetArrowCount1().invoke(entity, arrowsOnBody));
    }

    public int getBodyArrows() {
        try {
            return (int) LivingEntityAccessor.getMethodGetArrowCount1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setEffectColor(int color) {
        this.effectColor = effectColor;
        if (ServerVersion.supports(9)) {
            Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), LivingEntityAccessor.getFieldDATA_EFFECT_COLOR_ID().get(null), color));
            sendEntityData();
        } else {
            setMetadata(V1_8_LivingEntityMeta.POTION_EFFECT_COLOR.getIndex(), color);
        }
    }

    public int getEffectColor() {
        return effectColor;
    }

    public void setEffectsAsAmbients(Boolean asAmbients) {
        this.effectAsAmbient = asAmbients;
        if (ServerVersion.supports(9)) {
            Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), LivingEntityAccessor.getFieldDATA_EFFECT_AMBIENCE_ID().get(null), asAmbients));
            sendEntityData();
        } else {
            setMetadata(V1_8_LivingEntityMeta.IS_POTION_EFFECT_AMBIENT.getIndex(), asAmbients ? (byte) 1 : (byte) 0);
        }
    }

    public boolean areEffectsAsAmbients() {
        return effectAsAmbient;
    }

    public void resetEffects() {
        Ruom.run(() -> LivingEntityAccessor.getMethodRemoveEffectParticles1().invoke(entity));
        sendEntityData();
    }

    /**
     * @apiNote > 1.15
     */
    public void setStingerCount(int stingerCount) {
        if (!ServerVersion.supports(15)) return;
        if (!ServerVersion.supports(15)) return;
        Ruom.run(() -> LivingEntityAccessor.getMethodSetStingerCount1().invoke(entity));
        sendEntityData();
    }

    /**
     * @apiNote > 1.15
     */
    public int getStingerCount() {
        if (!ServerVersion.supports(15)) return -1;
        try {
            return (int) LivingEntityAccessor.getMethodGetStingerCount1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void startUsingItem() {
        startUsingItem(InteractionHand.MAIN_HAND);
    }

    public void startUsingItem(InteractionHand interactionHand) {
        if (ServerVersion.supports(9)) {
            Ruom.run(() -> {
                LivingEntityAccessor.getFieldUseItem().set(entity, interactionHand == InteractionHand.MAIN_HAND ? equipments.get(EquipmentSlot.MAINHAND) : equipments.get(EquipmentSlot.OFFHAND));
                if (ServerVersion.supports(13)) {
                    LivingEntityAccessor.getMethodSetLivingEntityFlag1().invoke(entity, 1, true);
                    LivingEntityAccessor.getMethodSetLivingEntityFlag1().invoke(entity, 2, interactionHand == InteractionHand.OFF_HAND);
                } else {
                    byte i = 1;
                    if (interactionHand == InteractionHand.OFF_HAND)
                        i |= 2;
                    SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), LivingEntityAccessor.getFieldDATA_LIVING_ENTITY_FLAGS().get(null), i);
                }
            });
            sendEntityData();
        } else {
            setPose(Pose.LEGACY_USE_ITEM, true);
        }
    }

    public void stopUsingItem() {
        if (ServerVersion.supports(9)) {
            Ruom.run(() -> LivingEntityAccessor.getMethodStopUsingItem1().invoke(entity));
            sendEntityData();
        } else {
            setPose(Pose.LEGACY_USE_ITEM, false);
        }
    }

    /**
     * @apiNote > 1.9
     */
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
