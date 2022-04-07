package me.mohamad82.ruom.world;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit;
import me.mohamad82.ruom.world.editsession.EditSession;
import me.mohamad82.ruom.world.editsession.UpdateRequiredEditSession;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Schematic {

    public final Map<Integer, Set<Vector3>> layerBlocks = new HashMap<>();
    private final Map<Vector3, BlockData> blockData = new HashMap<>();
    private final Clipboard clipboard;
    private final Location location;
    private final EditSession editSession;
    private final Random random = new Random();
    private final boolean ignoreAir;

    public Schematic(EditSession editSession, Clipboard clipboard, Location location, boolean ignoreAir) {
        this.editSession = editSession;
        this.clipboard = clipboard;
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());;
        this.ignoreAir = ignoreAir;
    }

    public CompletableFuture<Void> prepare() {
        return CompletableFuture.runAsync(() -> {
            Vector3 startLocation = Vector3UtilsBukkit.toVector3(location);
            Vector3 minPoint = Vector3.at(
                    clipboard.getMinimumPoint().getBlockX(),
                    clipboard.getMinimumPoint().getBlockY(),
                    clipboard.getMinimumPoint().getBlockZ()
            );
            Vector3 maxPoint = Vector3.at(
                    clipboard.getMaximumPoint().getBlockX(),
                    clipboard.getMaximumPoint().getBlockY(),
                    clipboard.getMaximumPoint().getBlockZ()
            );
            Vector3 clipboardCenter = Vector3UtilsBukkit.getCenter(minPoint, maxPoint);


            for (int y = minPoint.getBlockY(); y <= maxPoint.getBlockY(); y++) {
                for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
                    for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                        if (!layerBlocks.containsKey(y)) {
                            layerBlocks.put(y, new HashSet<>());
                        }

                        if (ignoreAir)
                            if (clipboard.getBlock(x, y, z).getMaterial().isAir()) continue;
                        Vector3 blockLocation = Vector3.at(x, y, z);
                        Vector3 intendedLocation = startLocation.clone().add(Vector3UtilsBukkit.getTravelDistance(clipboardCenter, blockLocation));

                        layerBlocks.get(y).add(intendedLocation);
                        blockData.put(intendedLocation, BukkitAdapter.adapt(clipboard.getBlock(BlockVector3.at(x, y, z))));
                    }
                }
            }
            Set<Integer> emptyLayers = new HashSet<>();
            for (Map.Entry<Integer, Set<Vector3>> entry : layerBlocks.entrySet()) {
                if (entry.getValue().isEmpty())
                    emptyLayers.add(entry.getKey());
            }
            emptyLayers.forEach(layerBlocks::remove);
        });
    }

    public void applyLayer(int layerIndex) {
        if (layerBlocks.containsKey(layerIndex)) {
            for (Vector3 blockLocation : layerBlocks.get(layerIndex)) {
                editSession.setBlock(blockLocation, blockData.get(blockLocation));
            }
            layerBlocks.remove(layerIndex);
        }
    }

    public void apply(Vector3 blockLocation) {
        if (blockData.containsKey(blockLocation)) {
            editSession.setBlock(blockLocation, blockData.get(blockLocation));
            remove(blockLocation);
        }
    }

    public void applyAll() {
        for (Map.Entry<Integer, Set<Vector3>> entry : layerBlocks.entrySet()) {
            for (Vector3 blockLocation : entry.getValue()) {
                editSession.setBlock(blockLocation, blockData.get(blockLocation));
            }
        }
        layerBlocks.clear();
        blockData.clear();
        updateIfRequired();
    }

    public CompletableFuture<Boolean> applyAll(int maxBlocksPerTick) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> updateFuture = new CompletableFuture<>();
        new BukkitRunnable() {
            public void run() {
                for (int applied = 0; applied < maxBlocksPerTick; applied++) {
                    if (blockData.isEmpty()) {
                        updateFuture.complete(true);
                        completableFuture.complete(true);
                        cancel();
                        return;
                    }
                    Vector3 blockLocation = blockData.keySet().iterator().next();
                    editSession.setBlock(blockLocation, blockData.get(blockLocation));
                    blockData.remove(blockLocation);
                }
            }
        }.runTaskTimer(Ruom.getPlugin(), 0, 1);

        updateFuture.whenComplete((bool, err) -> {
            updateIfRequired();
        });

        return completableFuture;
    }

    public Vector3 nextBlock(int layerIndex) {
        if (!layerBlocks.containsKey(layerIndex)) return null;
        return layerBlocks.get(layerIndex).iterator().next();
    }

    public Vector3 nearestBlock(int layerIndex, Vector3 location) {
        if (!layerBlocks.containsKey(layerIndex)) return null;
        double lowestDistance = Integer.MAX_VALUE;
        Vector3 nearestBlock = null;
        for (Vector3 blockLocation : layerBlocks.get(layerIndex)) {
            double distance = Vector3UtilsBukkit.distance(blockLocation, location);
            if (distance < lowestDistance) {
                lowestDistance = distance;
                nearestBlock = blockLocation;
            }
        }
        return nearestBlock;
    }

    public int nextLayerIndex() {
        return layerBlocks.keySet().iterator().next();
    }

    public int randomLayerIndex() {
        List<Integer> layers = new ArrayList<>(layerBlocks.keySet());
        return layers.get(random.nextInt(layers.size()));
    }

    public void updateIfRequired() {
        if (editSession instanceof UpdateRequiredEditSession) {
            Ruom.runSync(() -> {
                ((UpdateRequiredEditSession) editSession).apply();
                Ruom.runAsync(((UpdateRequiredEditSession) editSession)::update);
            });
        }
    }

    public BlockData getBlockData(Vector3 blockLocation) {
        return blockData.get(blockLocation);
    }

    public void removeFromLayer(int layerIndex, Vector3 blockLocation) {
        layerBlocks.get(layerIndex).remove(blockLocation);
        if (layerBlocks.get(layerIndex).isEmpty())
            layerBlocks.remove(layerIndex);
    }

    public void remove(Vector3 blockLocation) {
        for (int layerIndex : layerBlocks.keySet()) {
            if (layerBlocks.get(layerIndex).remove(blockLocation)) {
                if (layerBlocks.get(layerIndex).isEmpty())
                    layerBlocks.remove(layerIndex);
                break;
            }
        }
    }

    public boolean isDone() {
        return layerBlocks.isEmpty();
    }

}
