package me.mohamad82.ruom.utils;

import com.mojang.datafixers.util.Pair;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.npc.NPC;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

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

    public static Object getRespawnPacket(Object serverLevel, GameMode newGameMode, GameMode oldGameMode, boolean isFlat) {
        try {
            Object nmsNewGameMode = GameTypeAccessor.class.getMethod("getField" + newGameMode.toString().toUpperCase()).invoke(null);
            Object nmsOldGameMode = GameTypeAccessor.class.getMethod("getField" + oldGameMode.toString().toUpperCase()).invoke(null);
            if (ServerVersion.supports(19)) {
                return ClientboundRespawnPacketAccessor.getConstructor1().newInstance(
                        LevelAccessor.getMethodDimensionTypeId1().invoke(serverLevel),
                        LevelAccessor.getMethodDimension1().invoke(serverLevel),
                        ServerLevelAccessor.getMethodGetSeed1().invoke(serverLevel),
                        nmsNewGameMode,
                        nmsOldGameMode,
                        false,
                        isFlat,
                        (byte) 0,
                        Optional.empty()
                );
            } else {
                return ClientboundRespawnPacketAccessor.getConstructor0().newInstance(
                        LevelAccessor.getMethodDimensionType1().invoke(serverLevel),
                        LevelAccessor.getMethodDimension1().invoke(serverLevel),
                        ServerLevelAccessor.getMethodGetSeed1().invoke(serverLevel),
                        nmsNewGameMode,
                        nmsOldGameMode,
                        false,
                        isFlat
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Error(e);
        }
    }

    public static Object getPlayerInfoPacket(Object serverPlayer, PlayerInfoAction action) {
        try {
            if (ServerVersion.supports(20) || ServerVersion.getCompleteVersion().equals("v1_19_R2") || ServerVersion.getCompleteVersion().equals("v1_19_R3")) {
                if (action.equals(PlayerInfoAction.REMOVE_PLAYER)) {
                    return ClientboundPlayerInfoRemovePacketAccessor.getConstructor0().newInstance(ListUtils.toList(EntityAccessor.getMethodGetUUID1().invoke(serverPlayer)));
                } else {
                    return ClientboundPlayerInfoUpdatePacketAccessor.getMethodCreatePlayerInitializing1().invoke(null, ListUtils.toList(serverPlayer));
                }
            } else {
                Object serverPlayerArray = Array.newInstance(ServerPlayerAccessor.getType(), 1);
                Array.set(serverPlayerArray, 0, serverPlayer);

                return ClientboundPlayerInfoPacketAccessor.getConstructor0().newInstance(action.legacyNmsObject, serverPlayerArray);
            }
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
            if (ServerVersion.supports(13)) {
                return ClientboundMoveEntityPacket_i_PosAccessor.getConstructor0().newInstance(id,
                        (short) (x * 4096), (short) (y * 4096), (short) (z * 4096), true);
            } else {
                return ClientboundMoveEntityPacket_i_PosAccessor.getConstructor1().newInstance(id,
                        (long) (x * 4096), (long) (y * 4096), (long) (z * 4096), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityPosRotPacket(int id, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        try {
            if (ServerVersion.supports(13)) {
                return ClientboundMoveEntityPacket_i_PosRotAccessor.getConstructor0().newInstance(id,
                        (short) (x * 4096), (short) (y * 4096), (short) (z * 4096),
                        NMSUtils.getAngle(yaw), NMSUtils.getAngle(pitch), onGround);
            } else {
                return ClientboundMoveEntityPacket_i_PosRotAccessor.getConstructor1().newInstance(id,
                        (long) (x * 4096), (long) (y * 4096), (long) (z * 4096),
                        NMSUtils.getAngle(yaw), NMSUtils.getAngle(pitch), onGround);
            }
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

    public static Object getEntityEquipmentPacket(int id, NPC.EquipmentSlot equipmentSlot, Object nmsItem) {
        try {
            if (ServerVersion.supports(13)) {
                Pair<Object, Object> pair = new Pair<>(equipmentSlot.getNmsSlot(), nmsItem);
                List<Pair<Object, Object>> pairList = new ArrayList<>();
                pairList.add(pair);

                return ClientboundSetEquipmentPacketAccessor.getConstructor0().newInstance(id, pairList);
            } else {
                return ClientboundSetEquipmentPacketAccessor.getConstructor1().newInstance(id, equipmentSlot.getNmsSlot(), nmsItem);
            }
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

    /**
     * @apiNote > 1.9
     */
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

    public static Object getContainerSetContentPacket(int containerId, int stateId, List<ItemStack> items, ItemStack carriedItem) {
        try {
            List<Object> nmsItems = new ArrayList<>();
            for (ItemStack item : items) {
                nmsItems.add(NMSUtils.getNmsItemStack(item));
            }
            Object nonNullList = NonNullListAccessor.getConstructor0().newInstance(nmsItems, NMSUtils.getNmsEmptyItemStack());
            return ClientboundContainerSetContentPacketAccessor.getConstructor0().newInstance(
                    containerId,
                    stateId,
                    nonNullList,
                    NMSUtils.getNmsItemStack(carriedItem)
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getChatPacket(Component message, ChatType type, @Nullable UUID sender) {
        try {
            Object nmsComponent = MinecraftComponentSerializer.get().serialize(message);
            if (ServerVersion.supports(16)) {
                return ClientboundChatPacketAccessor.getConstructor0().newInstance(nmsComponent, type.getNmsObject(), sender == null ? UUID.randomUUID() : sender);
            } else {
                if (ServerVersion.supports(12)) {
                    return ClientboundChatPacketAccessor.getConstructor1().newInstance(nmsComponent, type.getNmsObject());
                } else {
                    return ClientboundChatPacketAccessor.getConstructor2().newInstance(nmsComponent, type.legacyId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    /**
     * @deprecated Chat preview was removed in 1.19.3
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static Object getChatPreviewPacket(int queryId, Component message) {
        if (!ServerVersion.supports(19)) return null;
        try {
            return ClientboundChatPreviewPacketAccessor.getConstructor0().newInstance(queryId, MinecraftComponentSerializer.get().serialize(message));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    /**
     * @deprecated Chat preview was removed in 1.19.3
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static Object getSetDisplayChatPreviewPacket(boolean enabled) {
        if (!ServerVersion.supports(19)) return null;
        try {
            return ClientboundSetDisplayChatPreviewPacketAccessor.getConstructor0().newInstance(enabled);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getPlayerTeamPacket(String name, @Nullable Component playerPrefix, @Nullable Component playerSuffix, NameTagVisibility nameTagVisibility, CollisionRule collisionRule, ChatColor color, Collection<String> players, boolean canSeeFriendlyInvisible, int method) {
        try {
            Object packet;
            if (ServerVersion.supports(17)) {
                Optional<Object> parameters;
                if (method == 0 || method == 2) {
                    Object playerTeam = PlayerTeamAccessor.getConstructor0().newInstance(null, name);
                    PlayerTeamAccessor.getFieldPlayerPrefix().set(playerTeam, MinecraftComponentSerializer.get().serialize(playerPrefix == null ? Component.empty() : playerPrefix));
                    PlayerTeamAccessor.getFieldPlayerSuffix().set(playerTeam, MinecraftComponentSerializer.get().serialize(playerSuffix == null ? Component.empty() : playerSuffix));
                    PlayerTeamAccessor.getFieldNameTagVisibility().set(playerTeam, nameTagVisibility.modernNmsObject);
                    PlayerTeamAccessor.getFieldCollisionRule().set(playerTeam, collisionRule.modernNmsObject);
                    PlayerTeamAccessor.getFieldColor().set(playerTeam, ChatFormattingAccessor.class.getMethod("getField" + color.name()).invoke(null));
                    ((Set<String>) PlayerTeamAccessor.getFieldPlayers().get(playerTeam)).addAll(players);
                    PlayerTeamAccessor.getFieldSeeFriendlyInvisibles().set(playerTeam, canSeeFriendlyInvisible);

                    parameters = Optional.of(ClientboundSetPlayerTeamPacket_i_ParametersAccessor.getConstructor0().newInstance(playerTeam));
                } else {
                    parameters = Optional.empty();
                }

                packet = ClientboundSetPlayerTeamPacketAccessor.getConstructor0().newInstance(
                        name,
                        method,
                        parameters,
                        players
                );
            } else {
                packet = ClientboundSetPlayerTeamPacketAccessor.getConstructor1().newInstance();

                ClientboundSetPlayerTeamPacketAccessor.getFieldName().set(packet, name);
                ClientboundSetPlayerTeamPacketAccessor.getFieldNametagVisibility().set(packet, nameTagVisibility.nmsName);
                ClientboundSetPlayerTeamPacketAccessor.getFieldColor().set(packet, ChatFormattingAccessor.class.getMethod("getField" + color.name()).invoke(null));
                ClientboundSetPlayerTeamPacketAccessor.getFieldPlayers().set(packet, players);
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

    public static Object getTeamCreatePacket(String name, @Nullable Component playerPrefix, @Nullable Component playerSuffix, NameTagVisibility nameTagVisibility, CollisionRule collisionRule, ChatColor color, Collection<String> players, boolean canSeeFriendlyInvisible) {
        return getPlayerTeamPacket(name, playerPrefix, playerSuffix, nameTagVisibility, collisionRule, color, players, canSeeFriendlyInvisible, 0);
    }

    public static Object getTeamRemovePacket(String name) {
        return getPlayerTeamPacket(name, null, null, NameTagVisibility.ALWAYS, CollisionRule.ALWAYS, ChatColor.RESET, Collections.emptyList(), false, 1);
    }

    public static Object getTeamModifyPacket(String name, @Nullable Component playerPrefix, @Nullable Component playerSuffix, NameTagVisibility nameTagVisibility, CollisionRule collisionRule, ChatColor color, boolean canSeeFriendlyInvisible) {
        return getPlayerTeamPacket(name, playerPrefix, playerSuffix, nameTagVisibility, collisionRule, color, Collections.emptyList(), canSeeFriendlyInvisible, 2);
    }

    public static Object getTeamAddPlayerPacket(String name, Collection<String> players) {
        return getPlayerTeamPacket(name, null, null, NameTagVisibility.ALWAYS, CollisionRule.ALWAYS, ChatColor.RESET, players, false, 3);
    }

    public static Object getTeamRemovePlayerPacket(String name, Collection<String> players) {
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

    public static Object getEntityCustomNameDataPacket(Object entity) {
        try {
            Object entityData = EntityAccessor.getMethodGetEntityData1().invoke(entity);

            return ClientboundSetEntityDataPacketAccessor.getConstructor1().newInstance(
                    EntityAccessor.getMethodGetId1().invoke(entity),
                    ListUtils.toList(
                            SynchedEntityDataAccessor.getMethodGetItem1().invoke(entityData, EntityAccessor.getFieldDATA_CUSTOM_NAME().get(null)),
                            SynchedEntityDataAccessor.getMethodGetItem1().invoke(entityData, EntityAccessor.getFieldDATA_CUSTOM_NAME_VISIBLE().get(null))
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityDataPacket(Object entity) {
        try {
            if (ServerVersion.supports(20) || ServerVersion.getCompleteVersion().equals("v1_19_R2") || ServerVersion.getCompleteVersion().equals("v1_19_R3")) {
                Class<?> int2ObjectClass = Class.forName("it.unimi.dsi.fastutil.ints.Int2ObjectMap");
                Method valuesMethod = int2ObjectClass.getMethod("values");
                Method iteratorMethod = int2ObjectClass.getMethod("values").getReturnType().getMethod("iterator");

                Iterator<?> iterator = (Iterator<?>) iteratorMethod.invoke(valuesMethod.invoke(SynchedEntityDataAccessor.getFieldItemsById().get(EntityAccessor.getMethodGetEntityData1().invoke(entity))));
                List<Object> list = new ArrayList<>();

                while (iterator.hasNext()) {
                    list.add(SynchedEntityData_i_DataItemAccessor.getMethodValue1().invoke(iterator.next()));
                }

                return ClientboundSetEntityDataPacketAccessor.getConstructor1().newInstance(
                        EntityAccessor.getMethodGetId1().invoke(entity),
                        list
                );
            } else {
                return ClientboundSetEntityDataPacketAccessor.getConstructor0().newInstance(
                        EntityAccessor.getMethodGetId1().invoke(entity),
                        EntityAccessor.getMethodGetEntityData1().invoke(entity),
                        true
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static Object getEntityDataPacket(int id, int metadataId, Object value) {
        try {
            Object synchedEntityData = SynchedEntityDataAccessor.getConstructor0().newInstance((Object) null);
            if (ServerVersion.supports(9)) {
                Object entityDataSerializer = NMSUtils.getEntityDataSerializer(value);
                SynchedEntityDataAccessor.getMethodDefine1().invoke(synchedEntityData, EntityDataSerializerAccessor.getMethodCreateAccessor1().invoke(entityDataSerializer, metadataId), value);
            } else {
                SynchedEntityDataAccessor.getMethodA1().invoke(synchedEntityData, metadataId, value);
            }

            return ClientboundSetEntityDataPacketAccessor.getConstructor0().newInstance(id, synchedEntityData, true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public enum PlayerInfoAction {
        ADD_PLAYER(ClientboundPlayerInfoUpdatePacket_i_ActionAccessor.getFieldADD_PLAYER(), ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldADD_PLAYER()),
        UPDATE_GAME_MODE(ClientboundPlayerInfoUpdatePacket_i_ActionAccessor.getFieldUPDATE_GAME_MODE(), ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldUPDATE_GAME_MODE()),
        UPDATE_LATENCY(ClientboundPlayerInfoUpdatePacket_i_ActionAccessor.getFieldUPDATE_LATENCY(), ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldUPDATE_LATENCY()),
        UPDATE_DISPLAY_NAME(ClientboundPlayerInfoUpdatePacket_i_ActionAccessor.getFieldUPDATE_DISPLAY_NAME(), ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldUPDATE_DISPLAY_NAME()),
        REMOVE_PLAYER(null, ClientboundPlayerInfoPacket_i_ActionAccessor.getFieldREMOVE_PLAYER());

        private final Object modernNmsObject;
        private final Object legacyNmsObject;

        PlayerInfoAction(Object modernNmsObject, Object legacyNmsObject) {
            this.modernNmsObject = modernNmsObject;
            this.legacyNmsObject = legacyNmsObject;
        }
    }

    public enum ChatType {
        CHAT((byte) 0),
        SYSTEM((byte) 1),
        GAME_INFO((byte) 2);

        private final byte legacyId;

        ChatType(byte legacyId) {
            this.legacyId = legacyId;
        }

        private Object getNmsObject() {
            switch (this) {
                case CHAT: return ChatTypeAccessor.getFieldCHAT();
                case SYSTEM: return ChatTypeAccessor.getFieldSYSTEM();
                case GAME_INFO: return ChatTypeAccessor.getFieldGAME_INFO();
            }
            return null;
        }
    }

    public enum NameTagVisibility {
        ALWAYS("always", Team_i_VisibilityAccessor.getFieldALWAYS()),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", Team_i_VisibilityAccessor.getFieldHIDE_FOR_OTHER_TEAMS()),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", Team_i_VisibilityAccessor.getFieldHIDE_FOR_OWN_TEAM()),
        NEVER("never", Team_i_VisibilityAccessor.getFieldNEVER());

        private final String nmsName;
        private final Object modernNmsObject; //1.17 and above

        NameTagVisibility(String nmsName, Object modernNmsObject) {
            this.nmsName = nmsName;
            this.modernNmsObject = modernNmsObject;
        }
    }

    public enum CollisionRule {
        ALWAYS("always", Team_i_CollisionRuleAccessor.getFieldALWAYS()),
        PUSH_OTHER_TEAMS("pushOtherTeams", Team_i_CollisionRuleAccessor.getFieldPUSH_OTHER_TEAMS()),
        PUSH_OWN_TEAM("pushOwnTeam", Team_i_CollisionRuleAccessor.getFieldPUSH_OWN_TEAM()),
        NEVER("never", Team_i_CollisionRuleAccessor.getFieldNEVER());

        private final String nmsName;
        private final Object modernNmsObject; //1.17 and above

        CollisionRule(String nmsName, Object modernNmsObject) {
            this.nmsName = nmsName;
            this.modernNmsObject = modernNmsObject;
        }
    }

}