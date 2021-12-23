package me.mohamad82.ruom.utils;

import me.mohamad82.ruom.vector.Vector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BlockUtils {

    public static Set<Vector3> cylinder(double radiusX, double radiusZ, boolean allDirections, boolean filled) {
        Set<Vector3> points = new HashSet<>();
        radiusX += 0.5;
        radiusZ += 0.5;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextX = 0;
        xLoop: for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextX;
            nextX = (x + 1) * invRadiusX;
            double nextZn = 0;
            for (int z = 0; z <= ceilRadiusZ; ++z) {
                final double zn = nextZn;
                nextZn = (z + 1) * invRadiusZ;

                double distanceSq = lengthSq(xn, zn);
                if (distanceSq > 1) {
                    if (z == 0) {
                        break xLoop;
                    }
                    break;
                }

                if (!filled) {
                    if (lengthSq(nextX, zn) <= 1 && lengthSq(xn, nextZn) <= 1) {
                        continue;
                    }
                }

                points.add(Vector3.at(x, 0, z));
                if (allDirections) {
                    points.add(Vector3.at(x, 0, -z));
                    points.add(Vector3.at(-x, 0, z));
                    points.add(Vector3.at(-x, 0, -z));
                }
            }
        }

        return points;
    }

    private static double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }

    public static void spawnBlockBreakParticles(Location blockLocation, Material material) {
        Location center = LocUtils.simplifyToCenter(blockLocation);
        for (int i = 0; i <= 30; i++) {
            blockLocation.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                    center.clone().add(getRandomInBlock(), getRandomInBlock() + 0.5, getRandomInBlock()),
                    2, material.createBlockData());
        }
    }

    private static float getRandomInBlock() {
        return (float) (new Random().nextInt(10) - 5) / 10;
    }

}
