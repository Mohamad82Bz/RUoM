package me.mohamad82.ruom.utils;

import com.mojang.datafixers.util.Pair;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.nmsaccessors.*;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class PacketUtils {

    public static Object getOpenScreenPacket(int containerId, int inventorySize, Component component) {
        try {
            if (ServerVersion.supports(13)) {
                return ClientboundOpenScreenPacketAccessor.getConstructor0().newInstance(containerId, MenuTypeAccessor.class.getMethod("getFieldGENERIC_9x" + (inventorySize / 9)).invoke(null), MinecraftComponentSerializer.get().serialize(component));
            } else {
                return ClientboundOpenScreenPacketAccessor.getConstructor1().newInstance(containerId, "minecraft:chest", component, inventorySize);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getRespawnPacket(Object serverLevel, Object newGameMode, Object oldGameMode, boolean isFlat, boolean copyMetadata) {
        try {
            return ClientboundRespawnPacketAccessor.getConstructor0().newInstance(LevelAccessor.getMethodDimensionType1().invoke(serverLevel),
                    LevelAccessor.getMethodDimension1().invoke(serverLevel), ServerLevelAccessor.getMethodGetSeed1().invoke(serverLevel),
                    newGameMode, oldGameMode, false, isFlat, copyMetadata);
        } catch (Exception e) {
            e.printStackTrace();
            return new Error(e);
        }
    }

    public static Object getPlayerInfoPacket(Object serverPlayer, PlayerInfoAction action) {
        try {
            Object serverPlayerArray = Array.newInstance(ServerPlayerAccessor.getType(), 1);
            Array.set(serverPlayerArray, 0, serverPlayer);

            return ClientboundPlayerInfoPacketAccessor.getConstructor0().newInstance(action.nmsObject, serverPlayerArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getAddPlayerPacket(Object player) {
        try {
            return ClientboundAddPlayerPacketAccessor.getConstructor0().newInstance(player);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getAddEntityPacket(Object entity, int data) {
        try {
            return ClientboundAddEntityPacketAccessor.getConstructor1().newInstance(entity, data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getAddEntityPacket(Object entity) {
        return getAddEntityPacket(entity, 0);
    }

    public static Object getHeadRotatePacket(Object entity, float yaw) {
        try {
            return ClientboundRotateHeadPacketAccessor.getConstructor0().newInstance(entity, NMSUtils.getAngle(yaw));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getRemoveEntitiesPacket(int... ids) {
        try {
            Object idArray = Array.newInstance(int.class, ids.length);
            for (int i = 0; i < ids.length; i++) {
                Array.set(idArray, i, ids[i]);
            }

            return ClientboundRemoveEntitiesPacketAccessor.getConstructor0().newInstance(idArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityRotPacket(int id, float yaw, float pitch) {
        try {
            return ClientboundMoveEntityPacket_i_RotAccessor.getConstructor0().newInstance(id, NMSUtils.getAngle(yaw), NMSUtils.getAngle(pitch), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityPosPacket(int id, double x, double y, double z) {
        try {
            return ClientboundMoveEntityPacket_i_PosAccessor.getConstructor0().newInstance(id,
                    (short) (x * 4096), (short) (y * 4096), (short) (z * 4096), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityPosRotPacket(int id, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        try {
            return ClientboundMoveEntityPacket_i_PosRotAccessor.getConstructor0().newInstance(id,
                    (short) (x * 4096), (short) (y * 4096), (short) (z * 4096),
                    NMSUtils.getAngle(yaw), NMSUtils.getAngle(pitch), onGround);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getTeleportEntityPacket(Object entity) {
        try {
            return ClientboundTeleportEntityPacketAccessor.getConstructor0().newInstance(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityVelocityPacket(int id, double x, double y, double z) {
        try {
            if (ServerVersion.supports(14)) {
                return ClientboundSetEntityMotionPacketAccessor.getConstructor0().newInstance(id, Vec3Accessor.getConstructor0().newInstance(x, y, z));
            } else {
                return ClientboundSetEntityMotionPacketAccessor.getConstructor1().newInstance(id, x, y, z);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getAnimatePacket(Object entity, int action) {
        try {
            return ClientboundAnimatePacketAccessor.getConstructor0().newInstance(entity, action);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getBlockDestructionPacket(Vector3 location, int stage) {
        try {
            return ClientboundBlockDestructionPacketAccessor.getConstructor0().newInstance(
                    location.hashCode(),
                    BlockPosAccessor.getConstructor0().newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                    stage
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityEquipmentPacket(int id, Object nmsEquipmentSlot, Object nmsItem) {
        try {
            Pair<Object, Object> pair = new Pair<>(nmsEquipmentSlot, nmsItem);
            List<Pair<Object, Object>> pairList = new ArrayList<>();
            pairList.add(pair);

            return ClientboundSetEquipmentPacketAccessor.getConstructor0().newInstance(id, pairList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getCollectItemPacket(int id, int collectorId, int itemAmount) {
        try {
            if (ServerVersion.supports(9)) {
                return ClientboundTakeItemEntityPacketAccessor.getConstructor0().newInstance(id, collectorId, itemAmount);
            } else {
                return ClientboundTakeItemEntityPacketAccessor.getConstructor1().newInstance(id, collectorId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getBlockEventPacket(Vector3 location, Material blockMaterial, int actionId, int actionParam) {
        try {
            return ClientboundBlockEventPacketAccessor.getConstructor0().newInstance(
                    BlockPosAccessor.getConstructor0().newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                    BlocksAccessor.getType().getField(blockMaterial.toString().toUpperCase()).get(null),
                    actionId, actionParam
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityPassengersPacket(Object entity, int... passengerIds) {
        try {
            Object packet = ClientboundSetPassengersPacketAccessor.getConstructor0().newInstance(entity);
            ClientboundSetPassengersPacketAccessor.getFieldPassengers().set(packet, passengerIds);

            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getChatPacket(Component message, ChatType type, @Nullable UUID sender) {
        try {
            if (ServerVersion.supports(16)) {
                return ClientboundChatPacketAccessor.getConstructor0().newInstance(MinecraftComponentSerializer.get().serialize(message), type.nmsObject, sender == null ? UUID.randomUUID() : sender);
            } else {
                return ClientboundChatPacketAccessor.getConstructor1().newInstance(MinecraftComponentSerializer.get().serialize(message), type.nmsObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getPlayerTeamPacket(String name, @Nullable Component playerPrefix, @Nullable Component playerSuffix, NameTagVisibility nameTagVisibility, CollisionRule collisionRule, ChatColor color, Collection<Player> players, boolean canSeeFriendlyInvisible, int method) {
        try {
            Object packet;
            if (ServerVersion.supports(17)) {
                //TODO
                packet = null;
            } else {
                packet = ClientboundSetPlayerTeamPacketAccessor.getConstructor1().newInstance();

                ClientboundSetPlayerTeamPacketAccessor.getFieldName().set(packet, name);
                ClientboundSetPlayerTeamPacketAccessor.getFieldNametagVisibility().set(packet, nameTagVisibility.nmsName);
                ClientboundSetPlayerTeamPacketAccessor.getFieldColor().set(packet, ChatFormattingAccessor.class.getMethod("getField" + color.name()).invoke(null));
                ClientboundSetPlayerTeamPacketAccessor.getFieldPlayers().set(packet, players.stream().map(Player::getName).collect(Collectors.toCollection(ArrayList::new)));
                ClientboundSetPlayerTeamPacketAccessor.getFieldMethod().set(packet, method);
                int options = 0;
                if (canSeeFriendlyInvisible) {
                    options |= 2;
                }
                ClientboundSetPlayerTeamPacketAccessor.getFieldOptions().set(packet, options);
                if (ServerVersion.supports(13)) {
                    ClientboundSetPlayerTeamPacketAccessor.getFieldDisplayName().set(packet, MinecraftComponentSerializer.get().serialize(Component.empty()));
                    ClientboundSetPlayerTeamPacketAccessor.getFieldPlayerPrefix().set(packet, MinecraftComponentSerializer.get().serialize(playerPrefix == null ? Component.empty() : playerPrefix));
                    ClientboundSetPlayerTeamPacketAccessor.getFieldPlayerSuffix().set(packet, MinecraftComponentSerializer.get().serialize(playerSuffix == null ? Component.empty() : playerSuffix));
                }
                if (ServerVersion.supports(9)) {
                    ClientboundSetPlayerTeamPacketAccessor.getFieldCollisionRule().set(packet, collisionRule.nmsName);
                }
            }

            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getTeamCreatePacket(String name, @Nullable Component playerPrefix, @Nullable Component playerSuffix, NameTagVisibility nameTagVisibility, CollisionRule collisionRule, ChatColor color, Collection<Player> players, boolean canSeeFriendlyInvisible) {
        return getPlayerTeamPacket(name, playerPrefix, playerSuffix, nameTagVisibility, collisionRule, color, players, canSeeFriendlyInvisible, 0);
    }

    public static Object getTeamRemovePacket(String name) {
        return getPlayerTeamPacket(name, null, null, NameTagVisibility.ALWAYS, CollisionRule.ALWAYS, ChatColor.RESET, Collections.emptyList(), false, 1);
    }

    public static Object getTeamModifyPacket(String name, @Nullable Component playerPrefix, @Nullable Component playerSuffix, NameTagVisibility nameTagVisibility, CollisionRule collisionRule, ChatColor color, boolean canSeeFriendlyInvisible) {
        return getPlayerTeamPacket(name, playerPrefix, playerSuffix, nameTagVisibility, collisionRule, color, Collections.emptyList(), canSeeFriendlyInvisible, 2);
    }

    public static Object getTeamAddPlayerPacket(String name, Collection<Player> players) {
        return getPlayerTeamPacket(name, null, null, NameTagVisibility.ALWAYS, CollisionRule.ALWAYS, ChatColor.RESET, players, false, 3);
    }

    public static Object getTeamRemovePlayerPacket(String name, Collection<Player> players) {
        return getPlayerTeamPacket(name, null, null, NameTagVisibility.ALWAYS, CollisionRule.ALWAYS, ChatColor.RESET, players, false, 4);
    }

    public static Object getEntityEventPacket(Object entity, byte eventId) {
        try {
            return ClientboundEntityEventPacketAccessor.getConstructor0().newInstance(entity, eventId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityDataPacket(Object entity) {
        try {
            return ClientboundSetEntityDataPacketAccessor.getConstructor0().newInstance(
                    EntityAccessor.getMethodGetId1().invoke(entity),
                    EntityAccessor.getMethodGetEntityData1().invoke(entity),
                    true
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityDataPacket(int id, int metadataId, Object value) {
        try {
            Object entityData = SynchedEntityDataAccessor.getConstructor0().newInstance((Object) null);
            Object entityDataSerializer = getEntityDataSerializer(value);
            SynchedEntityDataAccessor.getMethodDefine1().invoke(entityData, EntityDataSerializerAccessor.getMethodCreateAccessor1().invoke(entityDataSerializer, metadataId), value);

            return ClientboundSetEntityDataPacketAccessor.getConstructor0().newInstance(id, entityData, true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityDataSerializer(Object object) {
        try {
            switch (object.getClass().getSimpleName()) {
                case "Byte":
                    return EntityDataSerializersAccessor.getFieldBYTE().get(null);
                case "Integer":
                    return EntityDataSerializersAccessor.getFieldINT().get(null);
                case "Float":
                    return EntityDataSerializersAccessor.getFieldFLOAT().get(null);
                case "String":
                    return EntityDataSerializersAccessor.getFieldSTRING().get(null);
                case "Optional":
                    return EntityDataSerializersAccessor.getFieldOPTIONAL_COMPONENT().get(null);
                case "ItemStack":
                    return EntityDataSerializersAccessor.getFieldITEM_STACK().get(null);
                case "Boolean":
                    return EntityDataSerializersAccessor.getFieldBOOLEAN().get(null);
                default: {
                    if (object.getClass().equals(ComponentAccessor.getType())) {
                        return EntityDataSerializersAccessor.getFieldCOMPONENT();
                    } else if (object.getClass().equals(PoseAccessor.getType())) {
                        return EntityDataSerializersAccessor.getFieldPOSE();
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public enum PlayerInfoAction {
        ADD_PLAYER(ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldADD_PLAYER()),
        UPDATE_GAME_MODE(ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldUPDATE_GAME_MODE()),
        UPDATE_LATENCY(ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldUPDATE_LATENCY()),
        UPDATE_DISPLAY_NAME(ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldUPDATE_DISPLAY_NAME()),
        REMOVE_PLAYER(ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldREMOVE_PLAYER());

        private final Object nmsObject;

        PlayerInfoAction(Object nmsObject) {
            this.nmsObject = nmsObject;
        }
    }

    public enum ChatType {
        CHAT(ChatTypeAccessor.getFieldCHAT()),
        SYSTEM(ChatTypeAccessor.getFieldSYSTEM()),
        GAME_INFO(ChatTypeAccessor.getFieldGAME_INFO());

        private final Object nmsObject;

        ChatType(Object nmsObject) {
            this.nmsObject = nmsObject;
        }
    }

    public enum NameTagVisibility {
        ALWAYS("always"),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam"),
        NEVER("never");

        private final String nmsName;

        NameTagVisibility(String nmsName) {
            this.nmsName = nmsName;
        }
    }

    public enum CollisionRule {
        ALWAYS("always"),
        PUSH_OTHER_TEAMS("pushOtherTeams"),
        PUSH_OWN_TEAM("pushOwnTeam"),
        NEVER("never");

        private final String nmsName;

        CollisionRule(String nmsName) {
            this.nmsName = nmsName;
        }
    }

}