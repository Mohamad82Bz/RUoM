package me.mohamad82.ruom.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XSound;
import io.netty.channel.Channel;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.nmsaccessors.*;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.Future;

public class NMSUtils {

    private static Class<?> CRAFT_ITEM_STACK, CRAFT_PLAYER, CRAFT_WORLD, CRAFT_SERVER, CRAFT_BLOCK_STATE, CRAFT_PARTICLE, CRAFT_LIVING_ENTITY, CRAFT_ENTITY, CRAFT_BLOCK_ENTITY_STATE,
            CRAFT_CHUNK;

    private static Method CRAFT_ITEM_STACK_AS_NMS_COPY, CRAFT_ITEM_STACK_AS_BUKKIT_COPY, CRAFT_PLAYER_GET_HANDLE_METHOD, CRAFT_WORLD_GET_HANDLE_METHOD,
            CRAFT_SERVER_GET_SERVER_METHOD, CRAFT_BLOCK_STATE_GET_HANDLE_METHOD, CRAFT_PARTICLE_TO_NMS_METHOD, CRAFT_PARTICLE_TO_NMS_METHOD2,
            CRAFT_PARTICLE_TO_BUKKIT_METHOD, CRAFT_LIVING_ENTITY_GET_HANDLE_METHOD, CRAFT_ENTITY_GET_HANDLE_METHOD, ENTITY_GET_BUKKIT_ENTITY_METHOD,
            CRAFT_BLOCK_ENTITY_STATE_GET_TITE_ENTITY_METHOD, CRAFT_CHUNK_GET_HANDLE_METHOD;

    private static Field LIVING_ENTITY_DROPS_FIELD;

    static {
        try {
            {
                CRAFT_ITEM_STACK = ReflectionUtils.getCraftClass("inventory.CraftItemStack");
                CRAFT_PLAYER = ReflectionUtils.getCraftClass("entity.CraftPlayer");
                CRAFT_WORLD = ReflectionUtils.getCraftClass("CraftWorld");
                CRAFT_SERVER = ReflectionUtils.getCraftClass("CraftServer");
                CRAFT_BLOCK_STATE = ReflectionUtils.getCraftClass("block.CraftBlockState");
                if (ServerVersion.supports(9)) {
                    CRAFT_PARTICLE = ReflectionUtils.getCraftClass("CraftParticle");
                }
                CRAFT_LIVING_ENTITY = ReflectionUtils.getCraftClass("entity.CraftLivingEntity");
                CRAFT_ENTITY = ReflectionUtils.getCraftClass("entity.CraftEntity");
                if (ServerVersion.supports(9)) {
                    CRAFT_BLOCK_ENTITY_STATE = ReflectionUtils.getCraftClass("block.CraftBlockEntityState");
                }
                CRAFT_CHUNK = ReflectionUtils.getCraftClass("CraftChunk");
            }
            {
                CRAFT_PLAYER_GET_HANDLE_METHOD = CRAFT_PLAYER.getMethod("getHandle");
                CRAFT_ITEM_STACK_AS_NMS_COPY = CRAFT_ITEM_STACK.getMethod("asNMSCopy", ItemStack.class);
                CRAFT_ITEM_STACK_AS_BUKKIT_COPY = CRAFT_ITEM_STACK.getMethod("asBukkitCopy", ItemStackAccessor.getType());
                CRAFT_WORLD_GET_HANDLE_METHOD = CRAFT_WORLD.getMethod("getHandle");
                CRAFT_SERVER_GET_SERVER_METHOD = CRAFT_SERVER.getMethod("getServer");
                if (ServerVersion.supports(13)) {
                    //TODO: Find a way for 1.12 and below. LegacyVanillaEditSession won't work on 1.12 and below in this way.
                    CRAFT_BLOCK_STATE_GET_HANDLE_METHOD = CRAFT_BLOCK_STATE.getMethod("getHandle");
                }
                if (ServerVersion.supports(9)) {
                    CRAFT_PARTICLE_TO_NMS_METHOD = CRAFT_PARTICLE.getMethod("toNMS", Particle.class);
                    if (ServerVersion.supports(13)) {
                        CRAFT_PARTICLE_TO_NMS_METHOD2 = CRAFT_PARTICLE.getMethod("toNMS", Particle.class, Object.class);
                        CRAFT_PARTICLE_TO_BUKKIT_METHOD = CRAFT_PARTICLE.getMethod("toBukkit", ParticleOptionsAccessor.getType());
                    }
                }
                CRAFT_LIVING_ENTITY_GET_HANDLE_METHOD = CRAFT_LIVING_ENTITY.getMethod("getHandle");
                CRAFT_ENTITY_GET_HANDLE_METHOD = CRAFT_ENTITY.getMethod("getHandle");
                ENTITY_GET_BUKKIT_ENTITY_METHOD = EntityAccessor.getType().getMethod("getBukkitEntity");
                if (ServerVersion.supports(9)) {
                    CRAFT_BLOCK_ENTITY_STATE_GET_TITE_ENTITY_METHOD = CRAFT_BLOCK_ENTITY_STATE.getDeclaredMethod("getTileEntity");
                    CRAFT_BLOCK_ENTITY_STATE_GET_TITE_ENTITY_METHOD.setAccessible(true);
                }
                CRAFT_CHUNK_GET_HANDLE_METHOD = CRAFT_CHUNK.getMethod("getHandle");
            }
            {
                if (ServerVersion.supports(13)) {
                    LIVING_ENTITY_DROPS_FIELD = LivingEntityAccessor.getType().getField("drops");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte getAngle(float yawOrPitch) {
        return (byte) (yawOrPitch * 256 / 360);
    }

    public static Object getNmsItemStack(ItemStack item) {
        try {
            return CRAFT_ITEM_STACK_AS_NMS_COPY.invoke(null, item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getNmsEmptyItemStack() {
        try {
            return ItemStackAccessor.getFieldEMPTY().get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack getBukkitItemStack(Object nmsItem) {
        try {
            return (ItemStack) CRAFT_ITEM_STACK_AS_BUKKIT_COPY.invoke(null, nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Component getItemStackComponent(ItemStack item) {
        try {
            return MinecraftComponentSerializer.get().deserialize(ItemStackAccessor.getMethodGetDisplayName1().invoke(getNmsItemStack(item)));
        } catch (Exception e) {
            e.printStackTrace();
            return Component.empty();
        }
    }

    public static String getItemStackNBTJson(ItemStack item) {
        try {
            return ItemStackAccessor.getMethodSave1().invoke(getNmsItemStack(item), CompoundTagAccessor.getConstructor0().newInstance()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack getItemStackFromNBTJson(String nbtJson) {
        try {
            Object compoundTag = TagParserAccessor.getMethodParseTag1().invoke(null, nbtJson);
            if (ServerVersion.supports(13)) {
                return getBukkitItemStack(ItemStackAccessor.getMethodOf1().invoke(null, compoundTag));
            } else if (ServerVersion.supports(11)) {
                return getBukkitItemStack(ItemStackAccessor.getConstructor0().newInstance(compoundTag));
            } else {
                return getBukkitItemStack(ItemStackAccessor.getMethodCreateStack1().invoke(null, compoundTag));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getItemCategory(ItemStack item) {
        try {
            return (String) CreativeModeTabAccessor.getFieldLangId().get(ItemAccessor.getMethodGetItemCategory1().invoke(ItemStackAccessor.getMethodGetItem1().invoke(getNmsItemStack(item))));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack setDisplayName(ItemStack item, Component component) {
        try {
            Object nmsItem = NMSUtils.getNmsItemStack(item);
            CompoundTagAccessor.getMethodPutString1().invoke(getDisplayTag(nmsItem), ItemStackAccessor.getFieldTAG_DISPLAY_NAME().get(null), GsonComponentSerializer.gson().serialize(component.decoration(TextDecoration.ITALIC, false)));
            return NMSUtils.getBukkitItemStack(nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack setLore(ItemStack item, List<Component> lines) {
        try {
            Object nmsItem = NMSUtils.getNmsItemStack(item);
            List<Object> stringTagList = new ArrayList<>();
            for (Component line : lines) {
                stringTagList.add(StringTagAccessor.getConstructor0().newInstance(GsonComponentSerializer.gson().serialize(line.decoration(TextDecoration.ITALIC, false))));
            }
            CompoundTagAccessor.getMethodPut1().invoke(getDisplayTag(nmsItem), ItemStackAccessor.getFieldTAG_LORE().get(null), ListTagAccessor.getConstructor0().newInstance(stringTagList, TagAccessor.getFieldTAG_STRING().get(StringTagAccessor.getConstructor0().newInstance(""))));
            return NMSUtils.getBukkitItemStack(nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object getDisplayTag(Object nmsItem) {
        try {
            return CompoundTagAccessor.getMethodGetCompound1().invoke(ItemStackAccessor.getMethodGetTag1().invoke(nmsItem), ItemStackAccessor.getFieldTAG_DISPLAY().get(null));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setPlayerCamera(Player player, Entity entity) {
        setPlayerCamera(player, getNmsEntity(entity));
    }

    public static void setPlayerCamera(Player player) {
        setPlayerCamera(player, player);
    }

    public static void setPlayerCamera(Player player, Object nmsEntity) {
        Ruom.run(() -> ServerPlayerAccessor.getMethodSetCamera1().invoke(getServerPlayer(player), nmsEntity));
    }

    @Nullable
    public static ItemStack getPlayerUseItem(Player player) {
        try {
            Object useItem = LivingEntityAccessor.getMethodGetUseItem1().invoke(getServerPlayer(player));
            if (useItem == null || useItem.equals(getNmsEmptyItemStack())) return null;
            return getBukkitItemStack(useItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getPotion(ItemStack potion) {
        try {
            return PotionUtilsAccessor.getMethodGetPotion1().invoke(null, getNmsItemStack(potion));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getPotionColor(ItemStack potion) {
        try {
            return (int) PotionUtilsAccessor.getMethodGetColor1().invoke(null, getNmsItemStack(potion));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Object getParticleOptions(Particle particle) {
        try {
            return CRAFT_PARTICLE_TO_NMS_METHOD.invoke(null, particle);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @apiNote >= 1.13
     * @param particle The bukkit particle
     * @param data Data of the bukkit particle
     * @return Nms particle
     */
    public static Object getParticleOptions(Particle particle, Object data) {
        try {
            return CRAFT_PARTICLE_TO_NMS_METHOD2.invoke(null, particle, data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @apiNote >= 1.13
     * @param particleOptions The nms particle
     * @return Bukkit particle
     */
    public static Particle getBukkitParticle(Object particleOptions) {
        try {
            return (Particle) CRAFT_PARTICLE_TO_BUKKIT_METHOD.invoke(null, particleOptions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getNmsLivingEntity(LivingEntity livingEntity) {
        try {
            return CRAFT_LIVING_ENTITY_GET_HANDLE_METHOD.invoke(livingEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @apiNote >= 1.13
     * @param livingEntity The bukkit living entity
     * @return List of bukkit itemstack
     */
    public static List<ItemStack> getLivingEntityDrops(LivingEntity livingEntity) {
        try {
            return (List<ItemStack>) LIVING_ENTITY_DROPS_FIELD.get(getNmsLivingEntity(livingEntity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getNmsEntity(Entity entity) {
        try {
            return CRAFT_ENTITY_GET_HANDLE_METHOD.invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Entity getBukkitEntity(Object nmsEntity) {
        try {
            return (Entity) ENTITY_GET_BUKKIT_ENTITY_METHOD.invoke(nmsEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setEntityCustomName(Entity entity, Component component) {
        try {
            EntityAccessor.getMethodSetCustomName1().invoke(getNmsEntity(entity), MinecraftComponentSerializer.get().serialize(component));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getEntityDataAccessorId(Object entityDataAccessor) {
        try {
            if (!entityDataAccessor.getClass().equals(EntityDataAccessorAccessor.getType())) return -1;
            return (int) EntityDataAccessorAccessor.getMethodGetId1().invoke(entityDataAccessor);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void setSignLine(Sign sign, int line, Component component) {
        try {
            Object nmsComponent = MinecraftComponentSerializer.get().serialize(component);
            Object nmsSign = getNmsSign(sign);
            if (ServerVersion.supports(13)) {
                SignBlockEntityAccessor.getMethodSetMessage2().invoke(nmsSign, line - 1, nmsComponent);
            } else {
                Object[] lines = (Object[]) SignBlockEntityAccessor.getFieldMessages().get(nmsSign);
                lines[line - 1] = nmsComponent;
                SignBlockEntityAccessor.getFieldMessages().set(nmsSign, lines);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Component getSignLine(Sign sign, int line) {
        try {
            Object[] lines = (Object[]) SignBlockEntityAccessor.getFieldMessages().get(getNmsSign(sign));
            return MinecraftComponentSerializer.get().deserialize(lines[line - 1]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Component> getSignLines(Sign sign) {
        List<Component> list = new ArrayList<>();
        try {
            for (Object nmsComponent : (Object[]) SignBlockEntityAccessor.getFieldMessages().get(getNmsSign(sign))) {
                list.add(MinecraftComponentSerializer.get().deserialize(nmsComponent));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    public static void updateSign(Sign sign) {
        try {
            if (ServerVersion.supports(17)) {
                SignBlockEntityAccessor.getMethodMarkUpdated1().invoke(getNmsSign(sign));
            } else {
                sendPacket(sign.getBlock().getLocation().getWorld().getPlayers(), SignBlockEntityAccessor.getMethodGetUpdatePacket1().invoke(getNmsSign(sign)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @apiNote >= 1.13
     */
    public static Object getNmsSign(Sign sign) {
        try {
            return CRAFT_BLOCK_ENTITY_STATE_GET_TITE_ENTITY_METHOD.invoke(sign);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getNmsBlock(Block block) {
        try {
            if (ServerVersion.supports(16)) {
                return BlockBehaviour_i_BlockStateBaseAccessor.getMethodGetBlock1().invoke(
                        LevelAccessor.getMethodGetBlockState1().invoke(NMSUtils.getServerLevel(block.getWorld()), BlockPosAccessor.getConstructor0().newInstance(block.getX(), block.getY(), block.getZ()))
                );
            } else if (ServerVersion.supports(9)) {
                return BlockStateAccessor.getMethodGetBlock1().invoke(LevelAccessor.getMethodC1().invoke(NMSUtils.getServerLevel(block.getWorld()),
                        BlockPosAccessor.getConstructor0().newInstance(block.getX(), block.getY(), block.getZ())));
            } else {
                return LevelAccessor.getMethodC1().invoke(NMSUtils.getServerLevel(block.getWorld()),
                        BlockPosAccessor.getConstructor0().newInstance(block.getX(), block.getY(), block.getZ()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getPing(Player player) {
        try {
            return (int) ServerPlayerAccessor.getFieldLatency().get(getServerPlayer(player));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void setBodyArrows(Player player, int amount) {
        try {
            LivingEntityAccessor.getMethodSetArrowCount1().invoke(getServerPlayer(player), amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getBodyArrows(Player player) {
        try {
            return (int) LivingEntityAccessor.getMethodGetArrowCount1().invoke(getServerPlayer(player));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void playSound(Player player, Object soundEvent, float volume, float pitch) {
        if (!soundEvent.getClass().equals(SoundEventAccessor.getType())) {
            throw new IllegalArgumentException("Sound must be a SoundEvent object");
        }
        try {
            PlayerAccessor.getMethodPlaySound1().invoke(getServerPlayer(player), soundEvent, volume, pitch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getServerPlayer(Player player) {
        try {
            return CRAFT_PLAYER_GET_HANDLE_METHOD.invoke(player);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getServerLevel(World world) {
        try {
            return CRAFT_WORLD_GET_HANDLE_METHOD.invoke(world);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getLevelChunk(Chunk chunk) {
        try {
            return CRAFT_CHUNK_GET_HANDLE_METHOD.invoke(chunk);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getLightEngine(World world) {
        try {
            return LevelAccessor.getMethodGetLightEngine1().invoke(getServerLevel(world));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getDedicatedServer() {
        try {
            return CRAFT_SERVER_GET_SERVER_METHOD.invoke(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getBlockState(Material material) {
        try {
            return BlockAccessor.getMethodDefaultBlockState1().invoke(BlocksAccessor.getType().getField(material.toString().toUpperCase()).get(null));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getBlockState(BlockState blockState) {
        try {
            return CRAFT_BLOCK_STATE_GET_HANDLE_METHOD.invoke(null, blockState);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @apiNote >= 1.9, For 1.8 use {@link me.mohamad82.ruom.utils.SoundGroupUtils#getBlockSound(SoundGroupUtils.SoundType, Block)}
     */
    public static SoundGroup getSoundGroup(Material material) {
        try {
            return new SoundGroup(
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.BREAK, material),
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.STEP, material),
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.PLACE, material),
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.HIT, material),
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.FALL, material)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @apiNote >= 1.8
     */
    public static SoundGroup getSoundGroup(Block block) {
        try {
            return new SoundGroup(
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.BREAK, block),
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.STEP, block),
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.PLACE, block),
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.HIT, block),
                    SoundGroupUtils.getBlockSound(SoundGroupUtils.SoundType.FALL, block)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getServerGamePacketListener(Player player) {
        try {
            return ServerPlayerAccessor.getFieldConnection().get(getServerPlayer(player));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getConnection(Player player) {
        try {
            return ServerGamePacketListenerImplAccessor.getFieldConnection().get(getServerGamePacketListener(player));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float getAverageReceivedPackets(Player player) {
        try {
            return (float) ConnectionAccessor.getMethodGetAverageReceivedPackets1().invoke(getConnection(player));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static float getAverageSentPackets(Player player) {
        try {
            return (float) ConnectionAccessor.getMethodGetAverageSentPackets1().invoke(getConnection(player));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Object createResourceLocation(String string) {
        try {
            return ResourceLocationAccessor.getConstructor0().newInstance(string);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object createResourceLocation(String key, String value) {
        try {
            return ResourceLocationAccessor.getConstructor1().newInstance(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    /**
     * Returns the player's netty channel.
     * @param player The player.
     * @return The channel of the player.
     */
    public static Channel getChannel(Player player) {
        try {
            return (Channel) ConnectionAccessor.getFieldChannel().get(getConnection(player));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Disconnects (kicks) a player from the server.
     * @param player The player that is going to get disconnected.
     * @param component The component that player is going to see in their screen.
     */
    public static void disconnect(Player player, Component component) {
        try {
            ConnectionAccessor.getMethodDisconnect1().invoke(getConnection(player), MinecraftComponentSerializer.get().serialize(component));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects a player to a server over Internet.
     * @param player The player that is going to be transfered.
     * @param inetSocketAddress The address of destination server.
     * @param flag Declear that if socket channel should be EpollSocketChannel or NioSocketChannel.
     * @return The created connection in the new server.
     */
    @ApiStatus.Experimental
    public static Object connectToServer(Player player, InetSocketAddress inetSocketAddress, boolean flag) {
        try {
            return ConnectionAccessor.getMethodConnectToServer1().invoke(getConnection(player), inetSocketAddress, flag);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Connects to player to a local server.
     * @param player The player that is going to be transfered.
     * @param socketAddress The address of the local destination server.
     * @return The created connection in the new server.
     */
    @ApiStatus.Experimental
    public static Object connectToLocalServer(Player player, SocketAddress socketAddress) {
        try {
            return ConnectionAccessor.getMethodConnectToLocalServer1().invoke(getConnection(player), socketAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends a BlockDesturctionPacket to the target location.
     * @param viewers The viewers that are going to see the change.
     * @param location The location of the block.
     * @param stage The destruction stage between 1 and 9. Any number below 1 and above 9 will remove the destruction process.
     */
    public static void sendBlockDestruction(Set<Player> viewers, Vector3 location, int stage) {
        NMSUtils.sendPacket(viewers,
                PacketUtils.getBlockDestructionPacket(location, stage));
    }

    /**
     * Sets passengers of an entity with packets.
     * @param viewers The viewers that are going to see the change.
     * @param entity The entity.
     * @param passengers The passengers that are going to ride on the entity.
     */
    public static void setPassengers(Set<Player> viewers, Object entity, int... passengers) {
        NMSUtils.sendPacket(viewers,
                PacketUtils.getEntityPassengersPacket(entity, passengers));
    }

    /**
     * Sends a ClientboundBlockEventPacket to show a chest opening or closing animation.
     * @param viewers The viewers that are going to see the change.
     * @param blockLocation The block's location.
     * @param blockMaterial The block's material. It can be chest, trapped_chest, ender_chest or shulker_box. Other values will be ignored.
     * @param open Declear that chest should get opened or closed.
     */
    public static void sendChestAnimation(Set<Player> viewers, Vector3 blockLocation, Material blockMaterial, boolean open) {
        Object chestAnimationPacket = PacketUtils.getBlockEventPacket(blockLocation, blockMaterial, 1, open ? 1 : 0);

        sendPacket(viewers, chestAnimationPacket);
    }

    public static void spawnLightning(Set<Player> players, Location location, boolean sound) {
        try {
            sendPacket(players,
                    PacketUtils.getAddEntityPacket(LightningBoltAccessor.getConstructor0().newInstance(EntityTypeAccessor.getFieldLIGHTNING_BOLT(), getServerLevel(location.getWorld()))));
            if (sound) SoundContainer.soundContainer(XSound.ENTITY_LIGHTNING_BOLT_THUNDER).play(location, players);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void spawnLightning(Player player, Location location, boolean sound) {
        spawnLightning(Collections.singleton(player), location, sound);
    }

    public static void spawnLightning(Location location, boolean sound) {
        spawnLightning(new HashSet<>(location.getWorld().getPlayers()), location, sound);
    }

    /**
     * Sends one or more packets to a player.
     * @param player The player that is going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player.
     */
    public static void sendPacketSync(Player player, Object... packets) {
        try {
            Object connection = getServerGamePacketListener(player);
            for (Object packet : packets) {
                ServerGamePacketListenerImplAccessor.getMethodSend1().invoke(connection, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    /**
     * Sends one or more packets to a group of player.
     * @param players The players that are going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player(s).
     */
    public static void sendPacketSync(Collection<Player> players, Object... packets) {
        for (Player player : players) {
            sendPacketSync(player, packets);
        }
    }

    /**
     * Sends one or more packets to a player asynchronously. Packets are thread safe.
     * @param player The player that is going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player.
     */
    public static Future<?> sendPacket(Player player, Object... packets) {
        return Ruom.runEAsync(() -> sendPacketSync(player, packets));
    }

    /**
     * Sends one or more packets to a group of player asynchronously. Packets are thread safe.
     * @param players The players that are going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player(s).
     */
    public static Future<?> sendPacket(Collection<Player> players, Object... packets) {
        return Ruom.runEAsync(() -> sendPacketSync(players, packets));
    }

}
