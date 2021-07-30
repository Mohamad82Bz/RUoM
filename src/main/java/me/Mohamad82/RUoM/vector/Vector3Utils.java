package me.Mohamad82.RUoM.vector;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.World;

public class Vector3Utils {

    public static String toString(BlockVector3 vector3) {
        return vector3.getBlockX() + " : " + vector3.getBlockY() + " : " + vector3.getBlockZ();
    }

    public static BlockVector3 toBlockVector3(String vector3) {
        String[] vector3Split = vector3.split(" : ");
        return BlockVector3.at(Integer.parseInt(vector3Split[0]),Integer.parseInt(vector3Split[1]),
                Integer.parseInt(vector3Split[2]));
    }

    public static BlockVector3 toBlockVector3(Location loc) {
        return BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static Vector3 toVector3(String vector3) {
        String[] vector3Split = vector3.substring(1, vector3.length() - 1).split(", ");
        return Vector3.at(Double.parseDouble(vector3Split[0]), Double.parseDouble(vector3Split[1]), Double.parseDouble(vector3Split[2]));
    }

    public static Vector3 toVector3(Location loc) {
        return Vector3.at(loc.getX(), loc.getY(), loc.getZ());
    }

    public static Vector3 toVector3(BlockVector3 blockVector3) {
        return Vector3.at(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
    }

    public static BlockVector3 toBlockVector3(Vector3 vector3) {
        return BlockVector3.at(vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static Location toLocation(World world, BlockVector3 vector3) {
        return new Location(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static Vector3 simplifyToBlock(Vector3 vector3) {
        return Vector3.at(vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static Vector3 simplifyToCenter(Vector3 vector3) {
        return Vector3.at(vector3.getBlockX() + 0.5, vector3.getY(), vector3.getBlockZ() + 0.5);
    }

    public static Location toLocation(World world, Vector3 vector3) {
        return new Location(world, vector3.getX(), vector3.getY(), vector3.getZ());
    }

    public static BlockVector3 getTravelDistance(BlockVector3 from, BlockVector3 to) {
        Vector3 travelDistance = getTravelDistance(Vector3.at(from.getBlockX(), from.getBlockY(), from.getBlockZ()),
                Vector3.at(to.getBlockX(), to.getBlockY(), to.getBlockZ()));
        return BlockVector3.at(travelDistance.getBlockX(), travelDistance.getBlockY(), travelDistance.getBlockZ());
    }

    public static Vector3 getTravelDistance(Vector3 from, Vector3 to) {
        double xD = Math.abs(from.getX() - to.getX());
        double yD = Math.abs(from.getY() - to.getY());
        double zD = Math.abs(from.getZ() - to.getZ());

        if (from.getX() > to.getX())
            xD = xD * -1;
        if (from.getY() > to.getY())
            yD = yD * -1;
        if (from.getZ() > to.getZ())
            zD = zD * -1;

        return Vector3.at(xD, yD, zD);
    }

    public static BlockVector3 getCenter(BlockVector3 first, BlockVector3 second) {
        Vector3 center = getCenter(Vector3.at(first.getBlockX(), first.getBlockY(), first.getBlockZ()),
                Vector3.at(second.getBlockX(), second.getBlockY(), second.getBlockZ()));
        return BlockVector3.at(center.getBlockX(), center.getBlockY(), center.getBlockZ());
    }

    public static Vector3 getCenter(Vector3 first, Vector3 second) {
        double minX = Math.min(first.getX(), second.getX());
        double minY = Math.min(first.getY(), second.getY());
        double minZ = Math.min(first.getZ(), second.getZ());
        double x1 = Math.max(first.getX(), second.getX()) + 1;
        double y1 = Math.max(first.getY(), second.getY()) + 1;
        double z1 = Math.max(first.getZ(), second.getZ()) + 1;

        return Vector3.at(
                minX + (x1 - minX) / 2.0D,
                minY + (y1 - minY) / 2.0D,
                minZ + (z1 - minZ) / 2.0D);
    }

}
