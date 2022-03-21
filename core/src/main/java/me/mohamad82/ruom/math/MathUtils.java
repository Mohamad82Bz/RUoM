package me.mohamad82.ruom.math;

import me.mohamad82.ruom.math.vector.Vector3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MathUtils {

    public static float getCenterAngle(float a1, float a2) {
        return (360 + a2 + (((a1 - a2 + 180 + 360) % 360) - 180) / 2) % 360;
    }

    public static List<Vector3> circle(float radius, int points, boolean onFloor) {
        List<Vector3> locations = new ArrayList<>();
        for (int i = 1; i <= points; i++) {
            double teta = 2 * Math.PI * i / points;
            double sin = Math.sin(teta) * radius;
            double cos = Math.cos(teta) * radius;
            if (onFloor) {
                locations.add(Vector3.at(cos, 0, sin));
            } else {
                locations.add(Vector3.at(cos, sin, 0));
            }
        }
        return locations;
    }

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

}
