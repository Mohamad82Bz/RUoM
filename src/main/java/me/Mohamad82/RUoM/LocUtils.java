package me.Mohamad82.RUoM;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocUtils {

    public static String toString(Location loc) {
        String world = loc.getWorld().getName();
        double x = (int) loc.getX() + 0.5;
        double y = loc.getY();
        double z = (int) loc.getZ() + 0.5;
        double yaw = (int) loc.getYaw();
        double pitch = (int) loc.getPitch();

        return "World=" + world +
                " : " + "X=" + x +
                " : " + "Y=" + y +
                " : " + "Z=" + z +
                " : " + "Yaw=" + yaw +
                " : " + "Pitch=" + pitch;
    }

    public static Location toLocation(String locationString) {
        Location loc;
        try {
            String[] strSplit = locationString.split(" : ");

            String[] worldSplit = strSplit[0].split("=");
            String[] xSplit = strSplit[1].split("=");
            String[] ySplit = strSplit[2].split("=");
            String[] zSplit = strSplit[3].split("=");
            String[] yawSplit = strSplit[4].split("=");
            String[] pitchSplit = strSplit[5].split("=");

            World world = Bukkit.getWorld(worldSplit[1]);
            double x = (int) Double.parseDouble(xSplit[1]) + 0.5;
            double y = (int) Double.parseDouble(ySplit[1]);
            double z = (int) Double.parseDouble(zSplit[1]) + 0.5;
            float yaw = (int) Double.parseDouble(yawSplit[1]);
            float pitch = (int) Double.parseDouble(pitchSplit[1]);

            loc = new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            return null;
        }

        return loc;
    }

    public static String toStringBlock(Location loc) {
        String world = loc.getWorld().getName();
        double x = loc.getBlockX();
        double y = loc.getBlockY();
        double z = loc.getBlockZ();

        return "World=" + world +
                " : " + "X=" + x +
                " : " + "Y=" + y +
                " : " + "Z=" + z +
                " : " + "Yaw=" + 0 +
                " : " + "Pitch=" + 0;
    }

    public static Location simplifyLocationToCenter(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
    }

    public static Location simplifyLocationToBlock(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static Location getCenter(Location first, Location second) {
        int minX = Math.min(first.getBlockX(), second.getBlockX());
        int minY = Math.min(first.getBlockY(), second.getBlockY());
        int minZ = Math.min(first.getBlockZ(), second.getBlockZ());
        int x1 = Math.max(first.getBlockX(), second.getBlockX()) + 1;
        int y1 = Math.max(first.getBlockY(), second.getBlockY()) + 1;
        int z1 = Math.max(first.getBlockZ(), second.getBlockZ()) + 1;

        return new Location(first.getWorld(),
                minX + (x1 - minX) / 2.0D,
                minY + (y1 - minY) / 2.0D,
                minZ + (z1 - minZ) / 2.0D);
    }

}
