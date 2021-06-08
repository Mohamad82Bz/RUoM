package me.Mohamad82.RUoM;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.World;

public class Vector3Utils {

    public static String toString(BlockVector3 vector3) {
        return vector3.getBlockX() + " : " + vector3.getBlockY() + " : " + vector3.getBlockZ();
    }

    public static BlockVector3 toVector3(String vector3) {
        String[] vector3Split = vector3.split(" : ");
        return BlockVector3.at(Integer.parseInt(vector3Split[0]),Integer.parseInt(vector3Split[1]),
                Integer.parseInt(vector3Split[2]));
    }

    public static BlockVector3 toVector3(Location loc) {
        return BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static Location toLocation(World world, BlockVector3 vector3) {
        return new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static BlockVector3 getTravelDistance(BlockVector3 from, BlockVector3 to) {
        int xD = Math.abs(from.getBlockX() - to.getBlockX());
        int yD = Math.abs(from.getBlockY() - to.getBlockY());
        int zD = Math.abs(from.getBlockZ() - to.getBlockZ());

        if (from.getBlockX() > to.getBlockX())
            xD = xD * -1;
        if (from.getBlockY() > to.getBlockY())
            yD = yD * -1;
        if (from.getBlockZ() > to.getBlockZ())
            zD = zD * -1;

        return BlockVector3.at(xD, yD, zD);
    }

    public static BlockVector3 getCenter(BlockVector3 first, BlockVector3 second) {
        int minX = Math.min(first.getBlockX(), second.getBlockX());
        int minY = Math.min(first.getBlockY(), second.getBlockY());
        int minZ = Math.min(first.getBlockZ(), second.getBlockZ());
        int x1 = Math.max(first.getBlockX(), second.getBlockX()) + 1;
        int y1 = Math.max(first.getBlockY(), second.getBlockY()) + 1;
        int z1 = Math.max(first.getBlockZ(), second.getBlockZ()) + 1;

        return BlockVector3.at(
                minX + (x1 - minX) / 2.0D,
                minY + (y1 - minY) / 2.0D,
                minZ + (z1 - minZ) / 2.0D);
    }

}
