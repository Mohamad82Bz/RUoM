package me.mohamad82.ruom.world.editsession;

import me.mohamad82.ruom.math.vector.Vector3;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public class VanillaEditSession implements EditSession {

    private final World world;

    public VanillaEditSession(World world) {
        this.world = world;
    }

    @Override
    public void setBlock(Vector3 blockLocation, BlockData blockData) {
        world.getBlockAt(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ()).setBlockData(blockData);
    }

    @Override
    public void setBlock(Vector3 blockLocation, Material material) {
        world.getBlockAt(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ()).setType(material);
    }

}
