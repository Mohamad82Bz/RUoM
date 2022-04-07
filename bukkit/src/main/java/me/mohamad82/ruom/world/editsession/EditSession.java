package me.mohamad82.ruom.world.editsession;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public interface EditSession {

    public void setBlock(Vector3 blockLocation, BlockData blockData);

    public void setBlock(Vector3 blockLocation, Material material);

    public static NmsEditSession nms(World world) {
        return new NmsEditSession(world);
    }

    public static VanillaEditSession vanilla(World world) {
        return new VanillaEditSession(world);
    }

    public static WEEditSession worldEdit(World world) {
        return new WEEditSession(world);
    }

    public static EditSession best(World world) {
        if (Ruom.hasPlugin("FastAsyncWorldEdit") || Ruom.hasPlugin("WorldEdit")) {
            return worldEdit(world);
        } else if (ServerVersion.supports(14)) {
            return vanilla(world);
        } else {
            return nms(world);
        }
    }

}
