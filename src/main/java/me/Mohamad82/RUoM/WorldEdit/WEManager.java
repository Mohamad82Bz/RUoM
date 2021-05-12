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
import me.Mohamad82.RUoM.StringUtils;
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

    public boolean buildSchematic(CompletableFuture<Float> cf, Location location, File file, boolean ignoreAir) {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        ClipboardReader reader;
        Clipboard clipboard;
        World world = null;

        MilliCounter counter = new MilliCounter();
        MilliCounter counter2 = new MilliCounter();

        counter.start();

        if (core.equals(WEType.FAWE)) {
            world = FaweAPI.getWorld(location.getWorld().getName());
            if (world == null) return false;
        }

        if (format == null) {
            plugin.getLogger().severe("Unable to load Clipboard from file: " + file.getName());
            return false;
        }

        try {
            reader = format.getReader(new FileInputStream(file));
            clipboard = reader.read();

            if (clipboard == null) return false;
        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
            return false;
        }

        final World finalWorld = world;
        final BlockVector3 minPoint = clipboard.getMinimumPoint();
        final BlockVector3 maxPoint = clipboard.getMaximumPoint();
        final BlockVector3 clipboardCenter = Vector3Utils.toVector3(LocUtils.getCenter(Vector3Utils
                .toLocation(location.getWorld(), minPoint), Vector3Utils.toLocation(location.getWorld(), maxPoint)));

        if (core.equals(WEType.FAWE))
            new BukkitRunnable() {
                int y = 0;

                public void run() {

                    //Finish job when reached the highest Height
                    if (y > clipboard.getRegion().getHeight()) {
                        counter.stop();
                        cf.complete(counter.get());
                        cancel();
                        return;
                    }

                    BlockVectorSet blocks = getVectorSetFromClipboardAtY(clipboard, y, ignoreAir);

                    counter2.start();
                    //noinspection deprecation
                    try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(finalWorld, -1)) {
                        for (BlockVector3 block : blocks) {
                            BlockVector3 distance = Vector3Utils.getTravelDistance(clipboardCenter, block);
                            Location intendedLocation = location.clone();
                            intendedLocation.add(distance.getBlockX(), y, distance.getBlockZ());

                            editSession.setBlock(BlockVector3.at(intendedLocation.getBlockX(),
                                    intendedLocation.getBlockY(), intendedLocation.getBlockZ()), clipboard.getFullBlock(block));
                        }
                    } catch (MaxChangedBlocksException ignored) {
                    }
                    counter2.stop();
                    Bukkit.getConsoleSender().sendMessage(StringUtils.colorize(String.format("&6Layer &b%d took &3%sms &6time to perform.",
                            y, counter2.get())));

                    y++;
                    run();
                }
            }.runTaskAsynchronously(plugin);
        else if (core.equals(WEType.WORLDEDIT))
            new BukkitRunnable() {
                int y = 0;

                @Override
                public void run() {
                    //Finish job when reached the highest Height
                    if (y > clipboard.getRegion().getHeight()) {
                        cancel();
                        return;
                    }

                    List<BlockVector3> blocks = getVectorListFromClipboardAtY(clipboard, y, ignoreAir);

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

                    y++;
                    counter2.stop();
                }
            }.runTaskTimer(plugin, 0, 20);
        return true;
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

    public List<BlockVector3> getVectorListFromClipboardAtY(Clipboard clipboard, int y, boolean ignoreAir) {
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

        return blockVectors;
    }

}