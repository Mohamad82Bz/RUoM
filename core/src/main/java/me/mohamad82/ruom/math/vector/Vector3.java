package me.mohamad82.ruom.math.vector;

public class Vector3 implements Cloneable {

    private static final Vector3 ZERO = Vector3.at(0, 0, 0);

    public static Vector3 getZero() {
        return ZERO.clone();
    }

    private double x;
    private double y;
    private double z;

    private Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector3 at(double x, double y, double z) {
        return new Vector3(x, y, z);
    }

    public static Vector3 at(int x, int y, int z) {
        return new Vector3(x, y, z);
    }

    public int getBlockX() {
        if (x > 0) return (int) x;
        if (x < 0) return (int) Math.floor(x);
        return 0;
    }

    public int getBlockY() {
        if (y > 0) return (int) y;
        if (y < 0) return (int) Math.floor(y);
        return 0;
    }

    public int getBlockZ() {
        if (z > 0) return (int) z;
        if (z < 0) return (int) Math.floor(z);
        return 0;
    }

    public double getCenterX() {
        return getBlockX() + 0.5;
    }

    public double getCenterY() {
        return getBlockY() + 0.5;
    }

    public double getCenterZ() {
        return getBlockZ() + 0.5;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3 add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    public Vector3 add(Vector3 other) {
        return add(other.x, other.y, other.z);
    }

    public Vector3 subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public Vector3 subtract(Vector3 other) {
        return subtract(other.x, other.y, other.z);
    }

    @Override
    public String toString() {
        String x = this.getX() == (double) this.getBlockX() ? "" + this.getBlockX() : "" + this.getX();
        String y = this.getY() == (double) this.getBlockY() ? "" + this.getBlockY() : "" + this.getY();
        String z = this.getZ() == (double) this.getBlockZ() ? "" + this.getBlockZ() : "" + this.getZ();
        return String.format("(%s, %s, %s)", x, y, z);
    }

    @Override
    public Vector3 clone() {
        try {
            return (Vector3) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    @Override
    public int hashCode() {
        return this.getBlockX() ^ this.getBlockZ() << 12 ^ this.getBlockY() << 24;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3)) {
            return false;
        } else {
            Vector3 other = (Vector3) obj;
            return this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ();
        }
    }

}
