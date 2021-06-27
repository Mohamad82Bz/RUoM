package me.Mohamad82.RUoM;

public class Vector3 implements Cloneable {

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

    public int getBlockX() {
        return (int) Math.round(x);
    }

    public int getBlockY() {
        return (int) Math.round(y);
    }

    public int getBlockZ() {
        return (int) Math.round(z);
    }

    public double getCenterX() {
        return ((int) x) + 0.5;
    }

    public double getCenterY() {
        return getBlockY();
    }

    public double getCenterZ() {
        return ((int) z) + 0.5;
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
        x += other.getX();
        y += other.getY();
        z += other.getZ();

        return this;
    }

    @Override
    public String toString() {
        String x = this.getX() == (double) this.getBlockX() ? "" + this.getBlockX() : "" + this.getX();
        String y = this.getY() == (double) this.getBlockY() ? "" + this.getBlockY() : "" + this.getY();
        String z = this.getZ() == (double) this.getBlockZ() ? "" + this.getBlockZ() : "" + this.getZ();
        return String.format("(%s, %s, %s)", x, y, z);
    }

    public Vector3 clone() {
        try {
            return (Vector3) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3)) {
            return false;
        } else {
            Vector3 other = (Vector3) obj;
            return this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ();
        }
    }

}
