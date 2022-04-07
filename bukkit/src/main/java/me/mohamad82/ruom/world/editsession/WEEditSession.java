package me.mohamad82.ruom.world.editsession;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import me.mohamad82.ruom.math.vector.Vector3;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public class WEEditSession implements EditSession {

    private final World world;
    private final com.sk89q.worldedit.EditSession weEditSession;

    public WEEditSession(World world) {
        this.world = world;
        try {
            //noinspection deprecation
            weEditSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public void setBlock(Vector3 blockLocation, BlockData blockData) {
        weEditSession.setBlock(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ(), BukkitAdapter.adapt(blockData));
    }

    @Override
    public void setBlock(Vector3 blockLocation, Material material) {
        weEditSession.setBlock(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ(), BukkitAdapter.adapt(material.createBlockData()));
    }

}
