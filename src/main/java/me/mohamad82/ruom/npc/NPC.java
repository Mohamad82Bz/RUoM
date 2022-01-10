package me.mohamad82.ruom.npc;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.EquipmentSlotAccessor;
import me.mohamad82.ruom.nmsaccessors.PoseAccessor;
import me.mohamad82.ruom.nmsaccessors.Vec3Accessor;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import me.mohamad82.ruom.utils.Viewered;
import me.mohamad82.ruom.vector.Vector3;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class NPC extends Viewered {

    protected final Map<EquipmentSlot, Object> equipments = new HashMap<>();

    protected Object entity;
    protected int id;
    protected Location location;

    protected NPC() {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            equipments.put(equipmentSlot, NMSUtils.getNmsEmptyItemStack());
        }
    }

    protected void initialize(Object entity) {
        this.entity = entity;
        Ruom.run(() -> this.id = (int) EntityAccessor.getMethodGetId1().invoke(entity));
    }

    public void look(float yaw, float pitch) {
        Ruom.run(() -> EntityAccessor.getMethodSetRot1().invoke(entity, yaw, pitch));
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getEntityRotPacket(id, yaw, pitch),
                PacketUtils.getHeadRotatePacket(entity, yaw));
    }

    public boolean move(Vector3 vector3) {
        if (vector3.getX() > 8 || vector3.getY() > 8 || vector3.getZ() > 8) return false;
        setPosition(getPosition().add(vector3));
        NMSUtils.sendPacket(getViewers(), PacketUtils.getEntityPosPacket(id, vector3.getX(), vector3.getY(), vector3.getZ()));
        return true;
    }

    public CompletableFuture<Boolean> move(Vector3 vector3, final int inTicks) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        new BukkitRunnable() {
            int i = 0;
            public void run() {
                if (i >= inTicks) {
                    completableFuture.complete(true);
                    cancel();
                    return;
                }
                if (!move(Vector3.at(
                        vector3.getX() / inTicks,
                        vector3.getY() / inTicks,
                        vector3.getZ() / inTicks
                ))) {
                    completableFuture.complete(false);
                    cancel();
                }
                i++;
            }
        }.runTaskTimerAsynchronously(Ruom.getPlugin(), 0, 1);
        return completableFuture;
    }

    public boolean moveAndLook(Vector3 vector3, float yaw, float pitch) {
        if (vector3.getX() > 8 || vector3.getY() > 8 || vector3.getZ() > 8) return false;
        setPosition(getPosition().add(vector3));
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getEntityPosRotPacket(id, vector3.getX(), vector3.getY(), vector3.getZ(), yaw, pitch, true),
                PacketUtils.getHeadRotatePacket(entity, yaw)
        );
        return true;
    }

    public void teleport() {
        NMSUtils.sendPacket(getViewers(), PacketUtils.getTeleportEntityPacket(entity));
    }

    public void teleport(Vector3 vector3) {
        Ruom.run(() -> EntityAccessor.getMethodSetPos1().invoke(entity, vector3.getX(), vector3.getY(), vector3.getZ()));
        NMSUtils.sendPacket(getViewers(), PacketUtils.getTeleportEntityPacket(entity));
    }

    public void teleport(Vector3 vector3, float yaw, float pitch) {
        Ruom.run(() -> {
            EntityAccessor.getMethodSetPos1().invoke(entity, vector3.getX(), vector3.getY(), vector3.getZ());
            EntityAccessor.getMethodSetRot1().invoke(entity, yaw, pitch);
        });
        NMSUtils.sendPacket(getViewers(), PacketUtils.getTeleportEntityPacket(entity));
    }

    public void animate(Animation animation) {
        NMSUtils.sendPacket(getViewers(), PacketUtils.getAnimatePacket(entity, animation.getAction()));
    }

    protected void collect(int collectedEntityId, int collectorEntityId, int amount) {
        NMSUtils.sendPacket(getViewers(), PacketUtils.getCollectItemPacket(collectedEntityId, collectorEntityId, amount));
    }

    public void setVelocity(Vector3 vector3) {
        NMSUtils.sendPacket(getViewers(), PacketUtils.getEntityVelocityPacket(id, vector3.getX(), vector3.getY(), vector3.getZ()));
    }

    public void setEquipment(EquipmentSlot slot, ItemStack item) {
        Object nmsItem;
        if (item == null) {
            nmsItem = NMSUtils.getNmsEmptyItemStack();
        } else {
            nmsItem = NMSUtils.getNmsItemStack(item);
        }
        equipments.put(slot, nmsItem);
        NMSUtils.sendPacket(getViewers(), PacketUtils.getEntityEquipmentPacket(id, slot.nmsSlot, nmsItem));
    }

    public void setPose(Pose pose) {
        Ruom.run(() -> EntityAccessor.getMethodSetPose1().invoke(entity, pose.getNmsPose()));
        sendEntityData();
    }

    public void setGlowing(boolean glowing) {
        Ruom.run(() -> {
            if (ServerVersion.supports(17)) {
                EntityAccessor.getMethodSetGlowingTag1().invoke(entity, glowing);
            } else {
                EntityAccessor.getMethodSetGlowing1().invoke(entity, glowing);
            }
        });
        sendEntityData();
    }

    public boolean isGlowing() {
        try {
            if (ServerVersion.supports(17)) {
                return (boolean) EntityAccessor.getMethodHasGlowingTag1().invoke(entity);
            } else {
                return (boolean) EntityAccessor.getMethodIsGlowing1().invoke(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setCustomName(Component component) {
        Ruom.run(() -> EntityAccessor.getMethodSetCustomName1().invoke(entity, MinecraftComponentSerializer.get().serialize(component)));
        sendEntityData();
    }

    @Nullable
    public Component getCustomName() {
        try {
            Object component = EntityAccessor.getMethodGetCustomName1().invoke(entity);
            if (component == null) return null;
            return MinecraftComponentSerializer.get().deserialize(component);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setCustomNameVisible(boolean customNameVisible) {
        Ruom.run(() -> EntityAccessor.getMethodSetCustomNameVisible1().invoke(entity, customNameVisible));
        sendEntityData();
    }

    public boolean isCustomNameVisible() {
        try {
            return (boolean) EntityAccessor.getMethodIsCustomNameVisible1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setInvisible(boolean invisible) {
        Ruom.run(() -> EntityAccessor.getMethodSetInvisible1().invoke(entity, invisible));
        sendEntityData();
    }

    public boolean isInvisible() {
        try {
            return (boolean) EntityAccessor.getMethodIsInvisible1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setNoGravity(boolean noGravity) {
        Ruom.run(() -> EntityAccessor.getMethodSetNoGravity1().invoke(entity, noGravity));
        sendEntityData();
    }

    public boolean isNoGravity() {
        try {
            return (boolean) EntityAccessor.getMethodIsNoGravity1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setOnFire(boolean fire) {
        Ruom.run(() -> EntityAccessor.getMethodSetSharedFlag1().invoke(entity, 0, fire));
        sendEntityData();
    }

    public boolean isOnFire() {
        try {
            return (boolean) EntityAccessor.getMethodGetSharedFlag1().invoke(entity, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setSprinting(boolean sprinting) {
        Ruom.run(() -> EntityAccessor.getMethodSetSprinting1().invoke(entity, sprinting));
        sendEntityData();
    }

    public boolean isSprinting() {
        try {
            return (boolean) EntityAccessor.getMethodIsSprinting1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setTicksFrozen(int ticksFrozen) {
        if (!ServerVersion.supports(17)) return;
        Ruom.run(() -> EntityAccessor.getMethodSetTicksFrozen1().invoke(entity, ticksFrozen));
        sendEntityData();
    }

    public int getTicksFrozen() {
        try {
            return (int) EntityAccessor.getMethodGetTicksFrozen1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setMetadata(int metadataId, Object value) {
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getEntityDataPacket(id, metadataId, value));
    }

    public void setPassengers(int... passengerIds) {
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getEntityPassengersPacket(entity));
    }

    public void setUuid(UUID uuid) {
        Ruom.run(() -> EntityAccessor.getMethodSetUUID1().invoke(entity, uuid));
        sendEntityData();
    }

    public UUID getUuid() {
        try {
            return (UUID) EntityAccessor.getMethodGetUUID1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public Object getEntity() {
        return entity;
    }

    protected void sendEntityData() {
        Ruom.run(() -> NMSUtils.sendPacket(getViewers(), PacketUtils.getEntityDataPacket(entity)));
    }

    protected Object getEntityData() {
        try {
            return EntityAccessor.getMethodGetEntityData1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void setPosition(Vector3 position) {
        Ruom.run(() -> EntityAccessor.getMethodSetPos1().invoke(entity, position.getX(), position.getY(), position.getZ()));
    }

    public Vector3 getPosition() {
        try {
            Object vec3 = EntityAccessor.getFieldPosition().get(entity);
            return Vector3.at(
                    (double) Vec3Accessor.getMethodX1().invoke(vec3),
                    (double) Vec3Accessor.getMethodY1().invoke(vec3),
                    (double) Vec3Accessor.getMethodZ1().invoke(vec3)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum EquipmentSlot {
        MAINHAND(EquipmentSlotAccessor.getFieldMAINHAND()),
        OFFHAND(EquipmentSlotAccessor.getFieldOFFHAND()),
        HEAD(EquipmentSlotAccessor.getFieldHEAD()),
        CHEST(EquipmentSlotAccessor.getFieldCHEST()),
        FEET(EquipmentSlotAccessor.getFieldFEET()),
        LEGS(EquipmentSlotAccessor.getFieldLEGS());

        private final Object nmsSlot;

        EquipmentSlot(Object nmsSlot) {
            this.nmsSlot = nmsSlot;
        }
    }

    public enum Pose {
        STANDING(PoseAccessor.getFieldSTANDING()),
        FALL_FLYING(PoseAccessor.getFieldFALL_FLYING()),
        SLEEPING(PoseAccessor.getFieldSLEEPING()),
        SWIMMING(PoseAccessor.getFieldSWIMMING()),
        SPIN_ATTACK(PoseAccessor.getFieldSPIN_ATTACK()),
        CROUCHING(PoseAccessor.getFieldCROUCHING()),
        LONG_JUMPING(PoseAccessor.getFieldLONG_JUMPING()),
        DYING(PoseAccessor.getFieldDYING());

        private final Object nmsPose;

        Pose(Object nmsPose) {
            this.nmsPose = nmsPose;
        }

        public Object getNmsPose() {
            return nmsPose;
        }
    }

    public enum Animation {
        SWING_MAIN_ARM(0),
        TAKE_DAMAGE(1),
        LEAVE_BED(2),
        SWING_OFFHAND(3),
        CRITICAL_EFFECT(4),
        MAGIC_CRITICAL_EFFECT(5);

        private final int action;

        Animation(final int action) {
            this.action = action;
        }

        public int getAction() {
            return action;
        }
    }

}
