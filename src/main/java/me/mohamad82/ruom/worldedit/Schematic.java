package me.mohamad82.ruom.worldedit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.vector.Vector3;
import me.mohamad82.ruom.vector.Vector3Utils;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Schematic {

    private final Map<Integer, Set<Vector3>> layerBlocks = new HashMap<>();
    private final Map<Vector3, BlockData> blockData = new HashMap<>();
    private final Clipboard clipboard;
    private final Location location;
    private final EditSession editSession;
    private final boolean ignoreAir;

    public Schematic(Clipboard clipboard, Location location, boolean ignoreAir) {
        this.clipboard = clipboard;
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());;
        this.editSession = new EditSession(location.getWorld());
        this.ignoreAir = ignoreAir;
    }

    public CompletableFuture<Void> prepare() {
        return CompletableFuture.runAsync(() -> {
            Vector3 startLocation = Vector3Utils.toVector3(location);
            BlockVector3 minPoint = clipboard.getMinimumPoint();
            BlockVector3 maxPoint = clipboard.getMaximumPoint();
            Vector3 clipboardCenter = Vector3Utils.toVector3(Vector3Utils.getCenter(minPoint, maxPoint));

            Ruom.broadcast("startLocation: " + startLocation.toString());
            Ruom.broadcast("clipboardCenter: " + clipboardCenter.toString());
            Ruom.broadcast("origin: " + clipboard.getOrigin().toString());

            for (int y = minPoint.getBlockY(); y <= maxPoint.getBlockY(); y++) {
                for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
                    for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                        if (!layerBlocks.containsKey(y)) {
                            layerBlocks.put(y, new HashSet<>());
                        }

                        if (ignoreAir)
                            if (clipboard.getBlock(x, y, z).getMaterial().isAir()) continue;
                        Vector3 blockLocation = Vector3.at(x, y, z);
                        Vector3 intendedLocation = startLocation.clone().add(Vector3Utils.getTravelDistance(clipboardCenter, blockLocation));

                        layerBlocks.get(y).add(intendedLocation);
                        blockData.put(intendedLocation, BukkitAdapter.adapt(clipboard.getBlock(x, y, z)));
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

    public int nextLayerIndex() {
        return layerBlocks.keySet().iterator().next();
    }

    public int randomLayerIndex() {
        List<Integer> layers = new ArrayList<>(layerBlocks.keySet());
        return layers.get(new Random().nextInt(layers.size()));
    }

    public void update() {
        Ruom.runSync(editSession::apply);
        Ruom.runAsync(editSession::update);
    }

    public BlockData getBlockData(Vector3 blockLocation) {
        return blockData.get(blockLocation);
    }

    private void removeFromLayer(int layerIndex, Vector3 blockLocation) {
        layerBlocks.get(layerIndex).remove(blockLocation);
        if (layerBlocks.get(layerIndex).isEmpty())
            layerBlocks.remove(layerIndex);
    }

    private void remove(Vector3 blockLocation) {
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
