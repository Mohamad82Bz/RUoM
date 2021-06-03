package me.Mohamad82.RUoM.WorldEdit;

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
import me.Mohamad82.RUoM.LocUtils;
import me.Mohamad82.RUoM.MilliCounter;
import me.Mohamad82.RUoM.Vector3Utils;
import org.bukkit.Bukkit;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class WEManager {

    private static WEManager instance;

    public static WEManager getInstance() {
        return instance;
    }

    JavaPlugin plugin;
    WEType core;

    public WEManager(JavaPlugin plugin, WEType core) {
        instance = this;
        this.core = core;
        this.plugin = plugin;
    }

    public SchemProgress buildSchematic(Location location, File file, boolean ignoreAir) {
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
        final BlockVector3 clipboardCenter = Vector3Utils.toVector3(LocUtils.getCenter(
                Vector3Utils.toLocation(location.getWorld(), minPoint),
                Vector3Utils.toLocation(location.getWorld(), maxPoint)));

        schemProgress.setFailed(false);

        if (core.equals(WEType.FAWE)) {
            World world = FaweAPI.getWorld(location.getWorld().getName());
            new BukkitRunnable() {
                int y = 0;
                final int maxY = clipboard.getRegion().getHeight();

                public void run() {

                    //Finish job when reached the highest Height
                    if (y > clipboard.getRegion().getHeight()) {
                        counter.stop();
                        schemProgress.setTimeTaken(counter.get());
                        schemProgress.done();
                        cancel();
                        return;
                    }

                    BlockVectorSet blocks = getVectorSetFromClipboardAtY(clipboard, y, ignoreAir);

                    counter2.start();
                    //noinspection deprecation
                    try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                        for (BlockVector3 block : blocks) {
                            BlockVector3 distance = Vector3Utils.getTravelDistance(clipboardCenter, block);
                            Location intendedLocation = location.clone();
                            intendedLocation.add(distance.getBlockX(), y, distance.getBlockZ());

                            editSession.setBlock(BlockVector3.at(intendedLocation.getBlockX(),
                                    intendedLocation.getBlockY(), intendedLocation.getBlockZ()), clipboard.getFullBlock(block));
                        }
                    } catch (MaxChangedBlocksException ignored) {}
                    counter2.stop();
                    schemProgress.getLayerTimeTaken().add(counter2.get());

                    float progress = ((float) y / maxY) * 100;
                    schemProgress.setProgress(progress);

                    y++;
                    asyncQueue(this);
                }
            }.runTaskAsynchronously(plugin);
        }
        else if (core.equals(WEType.WORLDEDIT)) {
            new BukkitRunnable() {
                Future<List<BlockVector3>> futureBlocks = null;
                List<BlockVector3> blocks;
                int y = 0;
                final int maxY = clipboard.getRegion().getHeight();

                @Override
                public void run() {
                    //Finish job when reached the highest Height
                    if (y > clipboard.getRegion().getHeight()) {
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
                    float progress = ((float) y / maxY) * 100;
                    schemProgress.setProgress(progress);
                    schemProgress.getLayerTimeTaken().add(counter2.get());

                    y++;
                    syncQueue(this, 20);
                }
            }.runTask(plugin);
        }
        return schemProgress;
    }

    public void asyncQueue(BukkitRunnable runnable) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }, 10);
    }

    public void syncQueue(BukkitRunnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay);
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

    public BlockVectorSet getVectorSetFromClipboardAtY(Clipboard clipboard, int y, boolean ignoreAir) {
        BlockVector3 minPoint = clipboard.getMinimumPoint();
        BlockVector3 maxPoint = clipboard.getMaximumPoint();
        BlockVectorSet blockVector3s = new BlockVectorSet();
        int i = 0;

        for (int x = 0; x <= maxPoint.getBlockX() - minPoint.getBlockX(); x++) {
            for (int z = 0; z <= maxPoint.getBlockZ() - minPoint.getBlockZ(); z++) {
                BlockVector3 relative = minPoint.add(x, y, z);
                BlockState block = clipboard.getBlock(relative);

                if (ignoreAir)
                    if (block.getBlockType().getMaterial().isAir())
                        continue;

                blockVector3s.add(relative);
                i++;
            }
        }

        return blockVector3s;
    }

    public Future<List<BlockVector3>> getVectorListFromClipboardAtY(Clipboard clipboard, int y, boolean ignoreAir) {
        CompletableFuture<List<BlockVector3>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
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
            }
        });

        return future;
    }

}