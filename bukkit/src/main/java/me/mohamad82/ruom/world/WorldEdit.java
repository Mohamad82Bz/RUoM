package me.mohamad82.ruom.world;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.math.vector.Vector3;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;

public class WorldEdit {

    public static Optional<Clipboard> getClipboardFromSchematic(File schematicFile) {
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        if (clipboardFormat == null) {
            return Optional.empty();
        }
        ClipboardReader clipboardReader;
        Clipboard clipboard;

        try {
            clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile));
            clipboard = clipboardReader.read();
        } catch (IOException e) {
            Ruom.warn("Error while reading a schematic file.");
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(clipboard);
    }

    public static BlockArrayClipboard createClipboard(Vector3 firstPos, Vector3 secondPos, World world) {
        CuboidRegion cuboidRegion = new CuboidRegion(
                BlockVector3.at(firstPos.getBlockX(), firstPos.getBlockY(), firstPos.getBlockZ()),
                BlockVector3.at(secondPos.getBlockX(), secondPos.getBlockY(), secondPos.getBlockZ())
        );
        BukkitWorld bukkitWorld = new BukkitWorld(world);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(cuboidRegion);
        EditSession editSession;
        if (Ruom.hasPlugin("FastAsyncWorldEdit")) {
            editSession = new EditSessionBuilder(bukkitWorld).autoQueue(true).build();
        } else if (Ruom.hasPlugin("WorldEdit")) {
            try {
                Method editSessionMethod = com.sk89q.worldedit.WorldEdit.getInstance().getClass().getMethod("newEditSession", bukkitWorld.getClass());
                editSession = (EditSession) editSessionMethod.invoke(com.sk89q.worldedit.WorldEdit.getInstance(), bukkitWorld);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error(e);
            }
        } else {
            throw new IllegalStateException("Cannot create schematic without WorldEdit or FastAsyncWorldEdit.");
        }
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, cuboidRegion, clipboard, cuboidRegion.getMinimumPoint());
        try {
            Operations.complete(forwardExtentCopy);
            return clipboard;
        } catch (WorldEditException e) {
            Ruom.error("Failed to create schematic:");
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static void writeClipboard(File file, BlockArrayClipboard clipboard) {
        try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            clipboardWriter.write(clipboard);
        } catch (IOException e) {
            Ruom.error("Failed to write schematic to file:");
            e.printStackTrace();
        }
    }

}
