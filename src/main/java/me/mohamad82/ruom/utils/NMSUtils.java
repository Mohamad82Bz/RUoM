package me.mohamad82.ruom.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import io.netty.channel.Channel;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.vector.Vector3;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class NMSUtils {

    private static Class<?> CRAFT_ITEM_STACK, CRAFT_PLAYER, CRAFT_WORLD, CRAFT_SERVER, CRAFT_BLOCK_STATE, CRAFT_PARTICLE, CRAFT_LIVING_ENTITY, CRAFT_ENTITY;

    private static Method CRAFT_ITEM_STACK_AS_NMS_COPY, CRAFT_ITEM_STACK_AS_BUKKIT_COPY, CRAFT_PLAYER_GET_HANDLE_METHOD, CRAFT_WORLD_GET_HANDLE_METHOD,
            CRAFT_SERVER_GET_SERVER_METHOD, CRAFT_BLOCK_STATE_GET_HANDLE_METHOD, CRAFT_PARTICLE_TO_NMS_METHOD, CRAFT_PARTICLE_TO_NMS_METHOD2,
            CRAFT_PARTICLE_TO_BUKKIT_METHOD, CRAFT_LIVING_ENTITY_GET_HANDLE_METHOD, CRAFT_ENTITY_GET_HANDLE_METHOD, ENTITY_GET_BUKKIT_ENTITY_METHOD;

    private static Field LIVING_ENTITY_DROPS_FIELD;

    static {
        try {
            {
                CRAFT_ITEM_STACK = ReflectionUtils.getCraftClass("inventory.CraftItemStack");
                CRAFT_PLAYER = ReflectionUtils.getCraftClass("entity.CraftPlayer");
                CRAFT_WORLD = ReflectionUtils.getCraftClass("CraftWorld");
                CRAFT_SERVER = ReflectionUtils.getCraftClass("CraftServer");
                CRAFT_BLOCK_STATE = ReflectionUtils.getCraftClass("block.CraftBlockState");
                CRAFT_PARTICLE = ReflectionUtils.getCraftClass("CraftParticle");
                CRAFT_LIVING_ENTITY = ReflectionUtils.getCraftClass("entity.CraftLivingEntity");
                CRAFT_ENTITY = ReflectionUtils.getCraftClass("entity.CraftEntity");
            }
            {
                CRAFT_PLAYER_GET_HANDLE_METHOD = CRAFT_PLAYER.getMethod("getHandle");
                CRAFT_ITEM_STACK_AS_NMS_COPY = CRAFT_ITEM_STACK.getMethod("asNMSCopy", ItemStack.class);
                CRAFT_ITEM_STACK_AS_BUKKIT_COPY = CRAFT_ITEM_STACK.getMethod("asBukkitCopy", ItemStackAccessor.getType());
                CRAFT_WORLD_GET_HANDLE_METHOD = CRAFT_WORLD.getMethod("getHandle");
                CRAFT_SERVER_GET_SERVER_METHOD = CRAFT_SERVER.getMethod("getServer");
                if (ServerVersion.supports(13)) {
                    //TODO: Find a way for 1.12 and below. EditSession won't work on 1.12 and below in this way.
                    CRAFT_BLOCK_STATE_GET_HANDLE_METHOD = CRAFT_BLOCK_STATE.getMethod("getHandle");
                }
                if (ServerVersion.supports(9)) {
                    CRAFT_PARTICLE_TO_NMS_METHOD = CRAFT_PARTICLE.getMethod("toNMS", Particle.class);
                    CRAFT_PARTICLE_TO_NMS_METHOD2 = CRAFT_PARTICLE.getMethod("toNMS", Particle.class, Object.class);
                    CRAFT_PARTICLE_TO_BUKKIT_METHOD = CRAFT_PARTICLE.getMethod("toBukkit", ParticleOptionsAccessor.getType());
                }
                CRAFT_LIVING_ENTITY_GET_HANDLE_METHOD = CRAFT_LIVING_ENTITY.getMethod("getHandle");
                CRAFT_ENTITY_GET_HANDLE_METHOD = CRAFT_ENTITY.getMethod("getHandle");
                ENTITY_GET_BUKKIT_ENTITY_METHOD = EntityAccessor.getType().getMethod("getBukkitEntity");
            }
            {
                LIVING_ENTITY_DROPS_FIELD = LivingEntityAccessor.getType().getField("drops");
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

    public static String getItemStackNBTJson(ItemStack item) {
        try {
            Object nmsItem = getNmsItemStack(item);
            Object compoundTag = ItemStackAccessor.getMethodGetOrCreateTag1().invoke(nmsItem);

            return (String) CompoundTagAccessor.getMethodToString1().invoke(compoundTag);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack getItemStackFromNBTJson(String nbtJson) {
        try {
            return (ItemStack) ItemStackAccessor.getMethodOf1().invoke(TagParserAccessor.getMethodParseTag1().invoke(nbtJson));
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

    public static Object getParticleOptions(Particle particle, Object data) {
        try {
            return CRAFT_PARTICLE_TO_NMS_METHOD2.invoke(null, particle, data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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

        sendPacket(viewers,
                chestAnimationPacket);
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
    public static void sendPacketSync(Set<Player> players, Object... packets) {
        for (Player player : players) {
            sendPacketSync(player, packets);
        }
    }

    /**
     * Sends one or more packets to a player asynchronously. Packets are thread safe.
     * @param player The player that is going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player.
     */
    public static CompletableFuture<Void> sendPacket(Player player, Object... packets) {
        return CompletableFuture.runAsync(() -> {
            sendPacketSync(player, packets);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    /**
     * Sends one or more packets to a group of player asynchronously. Packets are thread safe.
     * @param players The players that are going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player(s).
     */
    public static CompletableFuture<Void> sendPacket(Set<Player> players, Object... packets) {
        return CompletableFuture.runAsync(() -> {
            sendPacketSync(players, packets);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

}
