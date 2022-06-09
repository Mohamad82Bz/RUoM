package me.mohamad82.ruom.npc;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit;
import me.mohamad82.ruom.metadata.entity.V1_12_EntityMeta;
import me.mohamad82.ruom.metadata.entity.V1_8_EntityMeta;
import me.mohamad82.ruom.metadata.entity.pose.V1_12_EntityPose;
import me.mohamad82.ruom.metadata.entity.pose.V1_8_EntityPose;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import me.mohamad82.ruom.utils.Viewable;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class NPC extends Viewable {

    protected final Map<EquipmentSlot, Object> equipments = new HashMap<>();
    private final Set<Pose> poses = new HashSet<>();

    private Component customName;
    private Vector3 position;

    protected Object entity;
    protected int id;
    protected boolean discarded = false;

    protected NPC() {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            equipments.put(equipmentSlot, NMSUtils.getNmsEmptyItemStack());
        }
    }

    protected void initialize(Object entity) {
        this.entity = entity;
        Ruom.run(() -> {
            this.id = (int) EntityAccessor.getMethodGetId1().invoke(entity);
            if (ServerVersion.supports(16)) {
                Object vec3 = EntityAccessor.getFieldPosition().get(entity);
                this.position = Vector3.at(
                        (double) Vec3Accessor.getMethodX1().invoke(vec3),
                        (double) Vec3Accessor.getMethodY1().invoke(vec3),
                        (double) Vec3Accessor.getMethodZ1().invoke(vec3)
                );
            } else {
                this.position = Vector3.at(
                        (double) EntityAccessor.getFieldLocX().get(entity),
                        (double) EntityAccessor.getFieldLocY().get(entity),
                        (double) EntityAccessor.getFieldLocZ().get(entity)
                );
            }
        });
    }

    public void look(float yaw, float pitch) {
        Ruom.run(() -> EntityAccessor.getMethodSetRot1().invoke(entity, yaw, pitch));
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getEntityRotPacket(id, yaw, pitch),
                PacketUtils.getHeadRotatePacket(entity, yaw));
    }

    public void lookAt(Vector3 location) {
        Location dirLocation = Vector3UtilsBukkit.toLocation(null, getPosition());
        dirLocation.setDirection(new Vector(location.getX(), location.getY(), location.getZ()));
        look(dirLocation.getYaw(), dirLocation.getPitch());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean move(Vector3 vector) {
        if (Math.sqrt((vector.getX() * vector.getX()) + (vector.getY() * vector.getY()) + (vector.getZ() * vector.getZ())) >= 8) return false;
        setPosition(getPosition().add(vector));
        NMSUtils.sendPacket(getViewers(), PacketUtils.getEntityPosPacket(id, vector.getX(), vector.getY(), vector.getZ()));
        return true;
    }

    public CompletableFuture<Boolean> move(Vector3 vector, final int inTicks) {
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
                        vector.getX() / inTicks,
                        vector.getY() / inTicks,
                        vector.getZ() / inTicks
                ))) {
                    completableFuture.complete(false);
                    cancel();
                }
                i++;
            }
        }.runTaskTimerAsynchronously(Ruom.getPlugin(), 0, 1);
        return completableFuture;
    }

    public boolean moveAndLook(Vector3 vector, float yaw, float pitch) {
        if (Math.sqrt((vector.getX() * vector.getX()) + (vector.getY() * vector.getY()) + (vector.getZ() * vector.getZ())) >= 8) return false;
        setPosition(getPosition().add(vector));
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getEntityPosRotPacket(id, vector.getX(), vector.getY(), vector.getZ(), yaw, pitch, true),
                PacketUtils.getHeadRotatePacket(entity, yaw)
        );
        return true;
    }

    public void teleport() {
        NMSUtils.sendPacket(getViewers(), PacketUtils.getTeleportEntityPacket(entity));
    }

    public void teleport(Vector3 location) {
        setPosition(location);
        NMSUtils.sendPacket(getViewers(), PacketUtils.getTeleportEntityPacket(entity));
    }

    public void teleport(Vector3 location, float yaw, float pitch) {
        Ruom.run(() -> {
            setPosition(location);
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

    public void setEquipment(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        Object nmsItem;
        if (item == null) {
            nmsItem = NMSUtils.getNmsEmptyItemStack();
        } else {
            nmsItem = NMSUtils.getNmsItemStack(item);
        }
        equipments.put(slot, nmsItem);
        NMSUtils.sendPacket(getViewers(), PacketUtils.getEntityEquipmentPacket(id, slot.nmsSlot, nmsItem));
    }

    public void setPose(Pose pose, boolean flag) {
        if (!pose.isSupported()) return;
        boolean changed;
        if (flag) {
            changed = poses.add(pose);
        } else {
            changed = poses.remove(pose);
            if (ServerVersion.supports(14) && !poses.contains(Pose.CROUCHING) && !poses.contains(Pose.SWIMMING)) {
                Ruom.run(() -> EntityAccessor.getMethodSetPose1().invoke(entity, Pose.STANDING.getModern()));
            }
        }
        if (changed) {
            if (ServerVersion.supports(14)) {
                if (poses.contains(Pose.SWIMMING)) {
                    Ruom.run(() -> EntityAccessor.getMethodSetPose1().invoke(entity, Pose.SWIMMING.getModern()));
                } else if (poses.contains(Pose.CROUCHING)) {
                    Ruom.run(() -> EntityAccessor.getMethodSetPose1().invoke(entity, Pose.CROUCHING.getModern()));
                }
            }
            setMetadata(0, Pose.getBitMasks(poses, true));
        }
    }

    public boolean hasPose(Pose pose) {
        return poses.contains(pose);
    }

    public void setCustomName(Component component) {
        this.customName = component;
        Object nmsComponent = MinecraftComponentSerializer.get().serialize(component);
        if (ServerVersion.supports(13)) {
            Ruom.run(() -> EntityAccessor.getMethodSetCustomName1().invoke(entity, nmsComponent));
        } else {
            setMetadata(ServerVersion.supports(12) ? V1_12_EntityMeta.CUSTOM_NAME.getIndex() : V1_8_EntityMeta.CUSTOM_NAME.getIndex(), nmsComponent);
        }
        sendEntityData();
    }

    @Nullable
    public Component getCustomName() {
        return customName;
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

    /**
     * @apiNote > 1.10
     */
    public void setNoGravity(boolean noGravity) {
        Ruom.run(() -> EntityAccessor.getMethodSetNoGravity1().invoke(entity, noGravity));
        sendEntityData();
    }

    /**
     * @apiNote > 1.10
     */
    public boolean isNoGravity() {
        try {
            return (boolean) EntityAccessor.getMethodIsNoGravity1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @apiNote > 1.17
     */
    public void setTicksFrozen(int ticksFrozen) {
        if (!ServerVersion.supports(17)) return;
        Ruom.run(() -> EntityAccessor.getMethodSetTicksFrozen1().invoke(entity, ticksFrozen));
        sendEntityData();
    }

    /**
     * @apiNote > 1.17
     */
    public int getTicksFrozen() {
        if (!ServerVersion.supports(17)) return 0;
        try {
            return (int) EntityAccessor.getMethodGetTicksFrozen1().invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setMetadata(int metadataId, Object value) {
        if (ServerVersion.supports(9)) {
            Object entityDataSerializer = NMSUtils.getEntityDataSerializer(value);
            Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), EntityDataSerializerAccessor.getMethodCreateAccessor1().invoke(entityDataSerializer, metadataId), value));
        } else {
            Ruom.run(() -> SynchedEntityDataAccessor.getMethodWatch1().invoke(getEntityData(), metadataId, value));
        }
        sendEntityData();
    }

    /**
     * @apiNote > 1.9
     */
    public void setPassengers(int... passengerIds) {
        if (!ServerVersion.supports(9)) return;
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getEntityPassengersPacket(entity));
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
        this.position = position;
        Ruom.run(() -> EntityAccessor.getMethodSetPos1().invoke(entity, position.getX(), position.getY(), position.getZ()));
    }

    public Vector3 getPosition() {
        return position.clone();
    }

    public void discard() {
        discarded = true;
        removeViewers(getViewers());
        equipments.clear();
        poses.clear();
        customName = null;
        entity = null;
    }

    @Override
    public void onAddViewers(Player... players) {
        if (discarded)
            throw new IllegalStateException("Cannot add viewers to a discarded npc.");
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
        STANDING(PoseAccessor.getFieldSTANDING(), V1_12_EntityPose.STANDING, V1_8_EntityPose.STANDING),
        ON_FIRE(null, V1_12_EntityPose.ON_FIRE, V1_8_EntityPose.ON_FIRE),
        CROUCHING(PoseAccessor.getFieldCROUCHING(), V1_12_EntityPose.CROUCHING, V1_8_EntityPose.CROUCHING),
        SPRINTING(null, V1_12_EntityPose.SPRINTING, V1_8_EntityPose.SPRINTING),
        SWIMMING(PoseAccessor.getFieldSWIMMING(), 0x10),
        INVISIBLE(null, V1_12_EntityPose.INVISIBLE, V1_8_EntityPose.INVISIBLE),
        GLOWING(null, V1_12_EntityPose.GLOWING),
        ELYTRA_FLYING(null, V1_12_EntityPose.ELYTRA_FLYING),
        LEGACY_USE_ITEM(null, null, V1_8_EntityPose.EATING_DRINKING_BLOCKING);

        private Object modern = null;
        private final int legacy;
        private final int superLegacy;

        Pose(Object modern, V1_12_EntityPose legacy, V1_8_EntityPose superLegacy) {
            if (ServerVersion.supports(14)) {
                this.modern = modern;
            }
            if (legacy != null) {
                this.legacy = legacy.getBitMask();
            } else {
                this.legacy = 0;
            }
            this.superLegacy = superLegacy.getBitMask();
        }

        Pose(Object modern, V1_12_EntityPose legacy) {
            if (ServerVersion.supports(14)) {
                this.modern = modern;
            }
            this.legacy = legacy.getBitMask();
            this.superLegacy = 0;
        }

        Pose(Object modern, int legacy, int superLegacy) {
            if (ServerVersion.supports(14)) {
                this.modern = modern;
            }
            this.legacy = legacy;
            this.superLegacy = superLegacy;
        }

        Pose(Object modern, int legacy) {
            if (ServerVersion.supports(14)) {
                this.modern = modern;
            }
            this.legacy = legacy;
            this.superLegacy = 0;
        }

        public Object getModern() {
            return modern;
        }

        public int getLegacy() {
            return legacy;
        }

        public int getSuperLegacy() {
            return superLegacy;
        }

        private boolean isSupported() {
            if (ServerVersion.supports(9) && legacy != 0) return true;
            else return superLegacy != 0;
        }

        public static int getBitMasks(boolean ignoreModern, Pose... poses) {
            int bitMask = 0;
            for (Pose pose : poses) {
                if (ignoreModern && ServerVersion.supports(14) && pose.getModern() != null) continue;
                if (ServerVersion.supports(9)) {
                    bitMask |= pose.legacy;
                } else {
                    bitMask |= pose.superLegacy;
                }
            }
            return (byte) bitMask;
        }

        public static byte getBitMasks(Set<Pose> poses, boolean ignoreModern) {
            return (byte) getBitMasks(ignoreModern, poses.toArray(new Pose[0]));
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
