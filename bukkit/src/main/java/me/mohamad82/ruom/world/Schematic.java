package me.mohamad82.ruom.world;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

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

    public Schematic(Clipboard clipboard, Location location, boolean ignoreAir) {
        this.clipboard = clipboard;
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());;
        this.editSession = new EditSession(location.getWorld());
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

    public void applyAndUpdate(Vector3 blockLocation) {
        if (blockData.containsKey(blockLocation)) {
            editSession.setBlockAndUpdate(blockLocation, blockData.get(blockLocation));
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
    }

    public Vector3 getRandomBlock(int layerIndex) {
        if (!layerBlocks.containsKey(layerIndex)) return null;
        return layerBlocks.get(layerIndex).iterator().next();
    }

    public Vector3 getNearestBlock(int layerIndex, Vector3 location) {
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
        Ruom.broadcast(layers.size() + "");
        return layers.get(random.nextInt(layers.size()));
    }

    public void update() {
        Ruom.runSync(() -> {
            editSession.apply();
            Ruom.runAsync(editSession::update);
        });
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
