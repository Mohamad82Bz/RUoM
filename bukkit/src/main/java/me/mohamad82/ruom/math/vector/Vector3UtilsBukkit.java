package me.mohamad82.ruom.math.vector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;

public class Vector3UtilsBukkit extends Vector3Utils {

    public static Vector3 toVector3(Location loc) {
        return Vector3.at(loc.getX(), loc.getY(), loc.getZ());
    }

    public static Location toLocation(World world, Vector3 vector3) {
        return new Location(world, vector3.getX(), vector3.getY(), vector3.getZ());
    }

    public static double distance(Vector3 first, Vector3 second) {
        return Math.sqrt(distanceSquared(first, second));
    }

    public static double distanceSquared(Vector3 first, Vector3 second) {
        return NumberConversions.square(first.getX() - second.getX()) + NumberConversions.square(first.getY() - second.getY()) + NumberConversions.square(first.getZ() - second.getZ());
    }

}
