package me.mohamad82.ruom.utils;

import me.mohamad82.ruom.nmsaccessors.RotationsAccessor;

public class Rotations {

    private final float x, y, z;

    private Rotations(float x, float y, float z) {
        this.x = !Float.isInfinite(x) && !Float.isNaN(x) ? x % 360.0F : 0.0F;
        this.y = !Float.isInfinite(y) && !Float.isNaN(y) ? y % 360.0F : 0.0F;
        this.z = !Float.isInfinite(z) && !Float.isNaN(z) ? z % 360.0F : 0.0F;
    }

    public static Rotations rotations(float x, float y, float z) {
        return new Rotations(x, y, z);
    }

    public static Rotations fromNmsRotations(Object nmsRotations) {
        try {
            return rotations(
                    (float) RotationsAccessor.getMethodGetX1().invoke(nmsRotations),
                    (float) RotationsAccessor.getMethodGetY1().invoke(nmsRotations),
                    (float) RotationsAccessor.getMethodGetZ1().invoke(nmsRotations)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Object toNmsRotations() {
        try {
            return RotationsAccessor.getConstructor0().newInstance(x, y, z);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
