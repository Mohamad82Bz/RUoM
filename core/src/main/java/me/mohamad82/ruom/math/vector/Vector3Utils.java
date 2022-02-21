package me.mohamad82.ruom.math.vector;

public class Vector3Utils {

    public static Vector3 toVector3(String vector3) {
        String[] vector3Split = vector3.substring(1, vector3.length() - 1).split(", ");
        return Vector3.at(Double.parseDouble(vector3Split[0]), Double.parseDouble(vector3Split[1]), Double.parseDouble(vector3Split[2]));
    }

    public static Vector3 simplifyToInt(Vector3 vector3) {
        return Vector3.at(vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }

    public static Vector3 simplifyToCenter(Vector3 vector3) {
        return Vector3.at(vector3.getBlockX() + 0.5, vector3.getY(), vector3.getBlockZ() + 0.5);
    }

    public static Vector3 getTravelDistance(Vector3 from, Vector3 to) {
        double xD = Math.abs(from.getX() - to.getX());
        double yD = Math.abs(from.getY() - to.getY());
        double zD = Math.abs(from.getZ() - to.getZ());
        if (from.getX() > to.getX()) xD = -xD;
        if (from.getY() > to.getY()) yD = -yD;
        if (from.getZ() > to.getZ()) zD = -zD;
        return Vector3.at(xD, yD, zD);
    }

    public static Vector3 getCenter(Vector3 firstPoint, Vector3 secondPoint) {
        Vector3 minPoint = getMinPoint(firstPoint, secondPoint);
        Vector3 maxPoint = getMaxPoint(firstPoint, secondPoint);
        Vector3 sum = minPoint.clone().add(maxPoint);
        return Vector3.at(sum.getX() / 2d, sum.getY() / 2d, sum.getZ() / 2d);
    }

    public static Vector3 getMinPoint(Vector3 first, Vector3 second) {
        return Vector3.at(
                Math.min(first.getX(), second.getX()),
                Math.min(first.getY(), second.getY()),
                Math.min(first.getZ(), second.getZ())
        );
    }

    public static Vector3 getMaxPoint(Vector3 first, Vector3 second) {
        return Vector3.at(
                Math.max(first.getX(), second.getX()),
                Math.max(first.getY(), second.getY()),
                Math.max(first.getZ(), second.getZ())
        );
    }

    public static boolean containsBetween(Vector3 first, Vector3 second, Vector3 location) {
        Vector3 minPoint = getMinPoint(first, second);
        Vector3 maxPoint = getMaxPoint(first, second);
        return location.getX() >= minPoint.getX() && location.getY() >= minPoint.getY() && location.getZ() >= minPoint.getZ() &&
                location.getX() <= maxPoint.getX() && location.getY() <= maxPoint.getY() && location.getZ() <= maxPoint.getZ();
    }

}
