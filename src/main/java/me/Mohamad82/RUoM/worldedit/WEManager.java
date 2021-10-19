package me.Mohamad82.RUoM.worldedit;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.collection.BlockVectorSet;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import me.Mohamad82.RUoM.Ruom;
import me.Mohamad82.RUoM.utils.LocUtils;
import me.Mohamad82.RUoM.utils.MilliCounter;
import me.Mohamad82.RUoM.vector.Vector3Utils;
import me.Mohamad82.RUoM.worldedit.enums.PastePattern;
import me.Mohamad82.RUoM.worldedit.enums.PasteSpeed;
import me.Mohamad82.RUoM.worldedit.enums.WEType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Slab;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class WEManager {

    final JavaPlugin plugin;
    final WEType core;

    private static WEManager instance;

    public static WEManager getInstance() {
        return instance;
    }

    public WEManager(JavaPlugin plugin, WEType core) {
        instance = this;
        this.core = core;
        this.plugin = plugin;
    }

    public SchemProgress buildSchematic(Location location, File file, PastePattern pattern, PasteSpeed speed, boolean ignoreAir) {
        SchemProgress schemProgress = new SchemProgress();

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        ClipboardReader reader;
        Clipboard clipboard;

        MilliCounter counter = new MilliCounter();
        MilliCounter counter2 = new MilliCounter();

        counter.start();

        if (format == null) {
            plugin.getLogger().severe("Unable to load Clipboard from file: " + file.getName());
            return schemProgress;
        }

        try {
            reader = format.getReader(new FileInputStream(file));
            clipboard = reader.read();

            if (clipboard == null) return schemProgress;
        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
            return schemProgress;
        }

        final BlockVector3 minPoint = clipboard.getMinimumPoint();
        final BlockVector3 maxPoint = clipboard.getMaximumPoint();
        final BlockVector3 clipboardCenter = Vector3Utils.toBlockVector3(LocUtils.getCenter(
                Vector3Utils.toLocation(location.getWorld(), minPoint),
                Vector3Utils.toLocation(location.getWorld(), maxPoint)));

        schemProgress.setFailed(false);

        int maxI = 0;
        if (pattern.equals(PastePattern.LAYER_XSIDE))
            maxI = clipboard.getRegion().getLength();
        else if (pattern.equals(PastePattern.LAYER_ZSIDE))
            maxI = clipboard.getRegion().getWidth();
        else if (pattern.equals(PastePattern.LAYER_YSIDE))
            maxI = clipboard.getRegion().getHeight();
        else if (pattern.equals(PastePattern.SQUARE_FROM_MID))
            maxI = Math.max(clipboard.getRegion().getLength(),
                    clipboard.getRegion().getWidth());
        else if (pattern.equals(PastePattern.CYLINDER_FROM_MID)) {
            maxI = Math.max(clipboard.getRegion().getLength(),
                    clipboard.getRegion().getWidth());
        }
        final int finalMaxI = maxI;

        int delay = 10;
        if (speed.equals(PasteSpeed.SLOW))
            delay = 30;
        else if (speed.equals(PasteSpeed.NORMAL))
            delay = 20;
        else if (speed.equals(PasteSpeed.FAST))
            delay = 10;
        else if (speed.equals(PasteSpeed.VERYFAST))
            delay = 5;
        final int finalDelay = delay;

        if (core.equals(WEType.FAWE)) {
            World world = FaweAPI.getWorld(location.getWorld().getName());

            new BukkitRunnable() {
                int i = 0;

                public void run() {
                    //Finish job when reached the highest Height
                    if (i > finalMaxI) {
                        counter.stop();
                        schemProgress.setTimeTaken(counter.get());
                        schemProgress.done();
                        cancel();
                        return;
                    }

                    BlockVectorSet blocks = getVectorSetFromClipboardInPattern(clipboard, pattern, i, ignoreAir);

                    counter2.start();
                    //noinspection deprecation
                    try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                        for (BlockVector3 block : blocks) {
                            BlockVector3 distance = Vector3Utils.getTravelDistance(clipboardCenter, block);
                            //BlockVector3 yDistance = Vector3Utils.getTravelDistance(block, Vector3Utils.toBlockVector3(location));
                            Location intendedLocation = location.clone();
                            intendedLocation.add(distance.getBlockX(), i, distance.getBlockZ());

                            editSession.setBlock(BlockVector3.at(intendedLocation.getBlockX(),
                                    intendedLocation.getBlockY(), intendedLocation.getBlockZ()), clipboard.getFullBlock(block));
                        }
                    } catch (MaxChangedBlocksException ignored) {}
                    counter2.stop();
                    schemProgress.getLayerTimeTaken().add(counter2.get());

                    float progress = ((float) i / finalMaxI) * 100;
                    schemProgress.setProgress(progress);

                    i++;
                    asyncQueue(this, finalDelay);
                }
            }.runTaskAsynchronously(plugin);
        }
        else if (core.equals(WEType.WORLDEDIT)) {
            new BukkitRunnable() {
                Future<List<BlockVector3>> futureBlocks = null;
                List<BlockVector3> blocks;
                int y = 0;

                @Override
                public void run() {
                    //Finish job when reached the highest Height
                    if (y > finalMaxI) {
                        counter.stop();
                        schemProgress.setTimeTaken(counter.get());
                        schemProgress.done();
                        cancel();
                        return;
                    }

                    if (futureBlocks == null) {
                        futureBlocks = getVectorListFromClipboardAtY(clipboard, y, ignoreAir);
                        syncQueue(this, 5);
                        return;
                    } else {
                        if (futureBlocks.isDone()) {
                            try {
                                blocks = futureBlocks.get();
                                futureBlocks = null;
                            } catch (Exception e) {
                                plugin.getLogger().severe("Something wrong happened during pasting a schematic. Consider using FAWE or report this issue" +
                                        " to the developer.");
                                e.printStackTrace();
                                cancel();
                            }
                        } else {
                            syncQueue(this, 5);
                        }
                    }

                    for (BlockVector3 block : blocks) {
                        BaseBlock baseBlock = clipboard.getFullBlock(block);
                        Map<Property<?>, Object> propertyMap = baseBlock.getStates();

                        BlockVector3 distance = Vector3Utils.getTravelDistance(clipboardCenter, block);
                        Location intendedLocation = location.clone();
                        intendedLocation.add(distance.getBlockX(), y, distance.getBlockZ());

                        BlockState blockState = clipboard.getBlock(block);
                        Material blockMaterial = BukkitAdapter.adapt(blockState.getBlockType());
                        Block bukkitBlock = intendedLocation.getBlock();

                        bukkitBlock.setType(blockMaterial);
                        org.bukkit.block.BlockState state = bukkitBlock.getState();

                        for (Property<?> property : propertyMap.keySet()) {
                            BlockData blockData = state.getBlockData();
                            Object value = propertyMap.get(property);

                            if (property.getName().equalsIgnoreCase("type") && blockData instanceof Slab) {
                                Slab slabData = (Slab) blockData;
                                Slab.Type slabType = Slab.Type.valueOf(value.toString().toUpperCase());

                                slabData.setType(slabType);
                                state.setBlockData(blockData);
                                state.update();
                            }

                            if (property.getName().equalsIgnoreCase("waterlogged") && blockData instanceof Waterlogged) {
                                Waterlogged waterData = (Waterlogged) blockData;
                                boolean waterlogged = Boolean.parseBoolean(value.toString());

                                waterData.setWaterlogged(waterlogged);
                                state.setBlockData(waterData);
                                state.update();
                            }
                        }
                    }

                    counter2.stop();
                    float progress = ((float) y / finalMaxI) * 100;
                    schemProgress.setProgress(progress);
                    schemProgress.getLayerTimeTaken().add(counter2.get());

                    y++;
                    syncQueue(this, finalDelay);
                }
            }.runTask(plugin);
        }

        return schemProgress;
    }

    public void asyncQueue(BukkitRunnable runnable, int delay) {
        Ruom.runAsync(runnable, delay);
    }

    public void syncQueue(BukkitRunnable runnable, int delay) {
        Ruom.runSync(runnable, delay);
    }

    public BlockArrayClipboard createClipboard(BlockVector3 firstPos, BlockVector3 secondPos, org.bukkit.World world) {
        CuboidRegion cuboidRegion = new CuboidRegion(firstPos, secondPos);
        BukkitWorld bukkitWorld = new BukkitWorld(world);

        BlockArrayClipboard clipboard = new BlockArrayClipboard(cuboidRegion);

        EditSession editSession = null;
        if (core.equals(WEType.WORLDEDIT))
            editSession = WorldEdit.getInstance().newEditSession(bukkitWorld);
        else if (core.equals(WEType.FAWE))
            //noinspection deprecation
            editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(bukkitWorld, -1);

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, cuboidRegion, clipboard, cuboidRegion.getMinimumPoint());
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }

        return clipboard;
    }

    public Clipboard getClipboard(File file) {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        ClipboardReader reader;
        Clipboard clipboard = null;

        if (format == null) {
            plugin.getLogger().severe("Unable to load Clipboard from file: " + file.getName());
        }

        try {
            reader = format.getReader(new FileInputStream(file));
            clipboard = reader.read();
        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
        }

        return clipboard;
    }

    public void createSchematic(File file, BlockArrayClipboard clipboard) {
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public BlockVectorSet getVectorSetFromClipboardInPattern(Clipboard clipboard, PastePattern pattern, int i, boolean ignoreAir) {
        BlockVector3 minPoint = clipboard.getMinimumPoint();
        BlockVector3 maxPoint = clipboard.getMaximumPoint();
        BlockVectorSet blockVector3s = new BlockVectorSet();

        if (pattern.equals(PastePattern.LAYER_YSIDE)) {
            for (int x = 0; x <= maxPoint.getBlockX() - minPoint.getBlockX(); x++) {
                for (int z = 0; z <= maxPoint.getBlockZ() - minPoint.getBlockZ(); z++) {
                    BlockVector3 relative = minPoint.add(x, i, z);

                    if (ignoreAir && isAir(clipboard.getBlock(relative)))
                        continue;

                    blockVector3s.add(relative);
                }
            }
        } else if (pattern.equals(PastePattern.LAYER_XSIDE)) {
            //X Side Pasting
            for (int y = 0; y <= clipboard.getRegion().getHeight(); y++) {
                for (int z = 0; z <= clipboard.getRegion().getWidth(); z++) {
                    BlockVector3 relative = minPoint.add(i, y, z);
                    BlockState block = clipboard.getBlock(relative);

                    if (ignoreAir && isAir(block))
                        continue;

                    blockVector3s.add(relative);
                }
            }
        } else if (pattern.equals(PastePattern.LAYER_ZSIDE)) {
            //Z Side Pasting
            for (int y = 0; y <= maxPoint.getBlockY() - minPoint.getBlockY(); y++) {
                for (int x = 0; x <= maxPoint.getBlockX() - minPoint.getBlockX(); x++) {
                    BlockVector3 relative = minPoint.add(x, y, i);
                    BlockState block = clipboard.getBlock(relative);

                    if (ignoreAir && isAir(block))
                        continue;

                    blockVector3s.add(relative);
                }
            }
        } else if (pattern.equals(PastePattern.SQUARE_FROM_MID)) {
            BlockVector3 center = Vector3Utils.getCenter(minPoint, maxPoint);
            for (int y = 0; y <= clipboard.getRegion().getHeight(); y++) {
                for (int x = -i + 1; x <= i - 1; x++) {
                    for (int z = -i + 1; z <= i - 1; z++) {
                        BlockVector3 relative = center.add(x, y, z);
                        if (!(x >= -(i - 1) + 1 && x <= i - 2 &&
                                z >= -(i - 1) + 1 && z <= i - 2)) {
                            blockVector3s.add(relative);
                        }
                    }
                }
            }
        } else if (pattern.equals(PastePattern.CYLINDER_FROM_MID)) {
            BlockVector3 center = Vector3Utils.getCenter(minPoint, maxPoint);
            int maxHeight = clipboard.getRegion().getHeight();
            blockVector3s.addAll(getFullBlockVectorsInCylinder(clipboard, center, i, i, maxHeight, ignoreAir));
            int debug = 0;
            for (BlockVector3 block : blockVector3s) {
                debug++;
            }
            /*if (ignoreAir) {
                BlockVectorSet blockVector3sToRemove = new BlockVectorSet();
                for (BlockVector3 blockVector3 : blockVector3s) {
                    if (isAir(clipboard.getBlock(blockVector3)))
                        blockVector3sToRemove.add(blockVector3);
                }
                blockVector3s.removeAll(blockVector3sToRemove);
            }*/
        }

        return blockVector3s;
    }

    public Future<List<BlockVector3>> getVectorListFromClipboardAtY(Clipboard clipboard, int y, boolean ignoreAir) {
        CompletableFuture<List<BlockVector3>> future = new CompletableFuture<>();

        Ruom.runAsync(() -> {
            BlockVector3 minPoint = clipboard.getMinimumPoint();
            BlockVector3 maxPoint = clipboard.getMaximumPoint();
            List<BlockVector3> blockVectors = new ArrayList<>();

            for (int x = 0; x <= maxPoint.getBlockX() - minPoint.getBlockX(); x++) {
                for (int z = 0; z <= maxPoint.getBlockZ() - minPoint.getBlockZ(); z++) {
                    BlockVector3 relative = minPoint.add(x, y, z);
                    BlockState block = clipboard.getBlock(relative);

                    if (ignoreAir)
                        if (block.getBlockType().getMaterial().isAir())
                            continue;

                    blockVectors.add(relative);
                }
            }

            future.complete(blockVectors);
        });

        return future;
    }

    public BlockVectorSet getFullBlockVectorsInCylinder(Clipboard clipboard, BlockVector3 center, double radiusX, double radiusZ, int heigh0, boolean ignoreAir) {
        BlockVectorSet blockVector3s = new BlockVectorSet();
        blockVector3s.addAll(getBlockVectorsInCylinder(clipboard, center, radiusX, radiusZ, false, 0, ignoreAir));
        BlockVectorSet additions = new BlockVectorSet();
        additions.addAll(getBlockVectorsInCylinder(clipboard, center, radiusX - 1, radiusZ - 1, false, 1, ignoreAir));
        additions.addAll(getBlockVectorsInCylinder(clipboard, center, radiusX, radiusZ, false, -1, ignoreAir));
        for (BlockVector3 addition : additions) {
            if (!(blockVector3s.contains(addition)))
                blockVector3s.add(addition);
        }

        return blockVector3s;
    }

    public BlockVectorSet getBlockVectorsInCylinder(Clipboard clipboard, BlockVector3 center,
                                                    double radiusX, double radiusZ, boolean filled, int addition, boolean ignoreAir) {
        BlockVectorSet blockVector3s = new BlockVectorSet();

        radiusX += 0.5;
        radiusZ += 0.5;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextX = 0;
        xLoop: for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextX;
            nextX = (x + 1) * invRadiusX;
            double nextZn = 0;
            for (int z = 0; z <= ceilRadiusZ; ++z) {
                final double zn = nextZn;
                nextZn = (z + 1) * invRadiusZ;

                double distanceSq = lengthSq(xn, zn);
                if (distanceSq > 1) {
                    if (z == 0) {
                        break xLoop;
                    }
                    break;
                }

                if (!filled) {
                    if (lengthSq(nextX, zn) <= 1 && lengthSq(xn, nextZn) <= 1) {
                        continue;
                    }
                }

                Set<BlockVector3> rotateBlocks = new HashSet<>();

                for (int y = clipboard.getMinimumPoint().getY(); y <= clipboard.getRegion().getHeight(); y++) {
                    BlockVector3 distance = Vector3Utils.getTravelDistance(center, BlockVector3.at(0, y, 0));
                    rotateBlocks.add(center.add(x + addition, distance.getBlockY(), z + addition));
                    rotateBlocks.add(center.add(-x + addition, distance.getBlockY(), z + addition));
                    rotateBlocks.add(center.add(x + addition, distance.getBlockY(), -z + addition));
                    rotateBlocks.add(center.add(-x + addition, distance.getBlockY(), -z + addition));

                    for (BlockVector3 blockVector3 : rotateBlocks) {
                        if (ignoreAir && isAir(clipboard.getBlock(blockVector3)))
                            continue;
                        blockVector3s.add(blockVector3);
                    }
                    rotateBlocks.clear();
                }
            }
        }

        return blockVector3s;
    }

    private static double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }

    private boolean isAir(BlockState block) {
        return block.getBlockType().getMaterial().isAir();
    }

}