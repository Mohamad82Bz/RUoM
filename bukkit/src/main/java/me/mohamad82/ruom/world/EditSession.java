package me.mohamad82.ruom.world;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EditSession {

    private final Map<Vector3, Object> blockStateModifications = new HashMap<>();
    private final Map<Vector3, BlockData> blockDataModifications = new HashMap<>();
    private final Set<Object> modifiedChunks = new HashSet<>();
    private final World world;
    private final Object serverLevel;
    private final Object lightEngine;

    public EditSession(World world) {
        this.world = world;
        serverLevel = NMSUtils.getServerLevel(world);
        lightEngine = NMSUtils.getLightEngine(world);
    }

    public void setBlockAndUpdate(Vector3 blockLocation, BlockData blockData) {
        Vector3UtilsBukkit.toLocation(world, blockLocation).getBlock().setBlockData(blockData);
    }

    public void setBlock(Vector3 blockLocation, BlockState blockState) {
        blockStateModifications.put(blockLocation, NMSUtils.getBlockState(blockState));
    }

    public void setBlock(Vector3 blockLocation, Material material) {
        blockStateModifications.put(blockLocation, NMSUtils.getBlockState(material));
    }

    public void setBlock(Vector3 blockLocation, BlockData blockData) {
        setBlock(blockLocation, blockData.getMaterial());
        if (!blockData.matches(blockData.getMaterial().createBlockData()))
            blockDataModifications.put(blockLocation, blockData);
    }

    public void apply() {
        try {
            for (Map.Entry<Vector3, BlockData> entry : blockDataModifications.entrySet()) {
                BlockState blockState = world.getBlockAt(entry.getKey().getBlockX(), entry.getKey().getBlockY(), entry.getKey().getBlockZ()).getState();
                blockState.setBlockData(entry.getValue());
                blockState.update(true, false);
            }
            for (Map.Entry<Vector3, Object> entry : blockStateModifications.entrySet()) {
                Object chunk = LevelAccessor.getMethodGetChunk1().invoke(serverLevel, entry.getKey().getBlockX() >> 4, entry.getKey().getBlockZ() >> 4);
                LevelChunkAccessor.getMethodSetBlockState1().invoke(
                        chunk,
                        BlockPosAccessor.getConstructor0().newInstance(entry.getKey().getBlockX(), entry.getKey().getBlockY(), entry.getKey().getBlockZ()),
                        entry.getValue(),
                        false);

                modifiedChunks.add(chunk);
            }

            for (Vector3 blockLocation : blockStateModifications.keySet()) {
                LevelLightEngineAccessor.getMethodCheckBlock1().invoke(lightEngine, BlockPosAccessor.getConstructor0().newInstance(
                        blockLocation.getBlockX(),
                        blockLocation.getBlockY(),
                        blockLocation.getBlockZ()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try {
            for (Object chunk : modifiedChunks) {
                Object chunkPos = ChunkAccessAccessor.getMethodGetPos1().invoke(chunk);

                Object chunkLoadPacket, lightUpdatePacket;
                if (ServerVersion.supports(18)) {
                    chunkLoadPacket = ClientboundLevelChunkWithLightPacketAccessor.getConstructor0().newInstance(chunk, lightEngine, null, null, true);
                    lightUpdatePacket = null;
                } else {
                    if (ServerVersion.supports(17)) {
                        chunkLoadPacket = ClientboundLevelChunkPacketAccessor.getConstructor0().newInstance(chunk);
                        lightUpdatePacket = ClientboundLightUpdatePacketAccessor.getConstructor0().newInstance(
                                chunkPos, lightEngine, null, null, true
                        );
                    } else {
                        chunkLoadPacket = ClientboundLevelChunkPacketAccessor.getConstructor1().newInstance(chunk, 65535);
                        lightUpdatePacket = ClientboundLightUpdatePacketAccessor.getConstructor1().newInstance(
                                chunkPos, lightEngine, true
                        );
                    }
                }

                int chunkMinX = (int) ChunkPosAccessor.getMethodGetMinBlockX1().invoke(chunkPos);
                int chunkMaxX = (int) ChunkPosAccessor.getMethodGetMaxBlockX1().invoke(chunkPos);
                int chunkMinZ = (int) ChunkPosAccessor.getMethodGetMinBlockZ1().invoke(chunkPos);
                int chunkMaxZ = (int) ChunkPosAccessor.getMethodGetMaxBlockZ1().invoke(chunkPos);

                Location location = new Location(
                        world,
                        (float) (chunkMinX + chunkMaxX) / 2,
                        0,
                        (float) (chunkMinZ + chunkMaxZ) / 2
                );

                getNearbyPlayers(location).whenComplete((nearbyPlayers, error) -> {
                    NMSUtils.sendPacket(nearbyPlayers, chunkLoadPacket);
                    if (lightUpdatePacket != null)
                        NMSUtils.sendPacket(nearbyPlayers, lightUpdatePacket);
                });
            }

            blockStateModifications.clear();
            modifiedChunks.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CompletableFuture<Set<Player>> getNearbyPlayers(Location location) {
        CompletableFuture<Set<Player>> completableFuture = new CompletableFuture<>();
        Ruom.runSync(() -> {
            Set<Player> nearbyPlayers = new HashSet<>();
            for (Entity nearbyEntity : location.getWorld().getNearbyEntities(location,
                    (Bukkit.getViewDistance() + 1) * 16, location.getWorld().getMaxHeight(), (Bukkit.getViewDistance() + 1) * 16)) {
                if (nearbyEntity.getType().equals(EntityType.PLAYER)) {
                    nearbyPlayers.add((Player) nearbyEntity);
                }
            }
            completableFuture.complete(nearbyPlayers);
        });
        return completableFuture;
    }

}
