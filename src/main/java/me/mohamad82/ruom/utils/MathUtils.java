package me.mohamad82.ruom.utils;

public class MathUtils {

    public static float getCenterAngle(float a1, float a2) {
        return (360 + a2 + (((a1 - a2 + 180 + 360) % 360) - 180) / 2) % 360;
    }

}
