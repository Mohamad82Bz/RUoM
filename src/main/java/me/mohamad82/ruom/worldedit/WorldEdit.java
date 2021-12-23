package me.mohamad82.ruom.worldedit;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import me.mohamad82.ruom.Ruom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

}
