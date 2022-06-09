package me.mohamad82.ruom.metadata.entity.pose;

public enum V1_12_EntityPose {
    STANDING(0),
    ON_FIRE(0x01),
    CROUCHING(0x02),
    SPRINTING(0x08),
    INVISIBLE(0x20),
    GLOWING(0x40),
    ELYTRA_FLYING(0x80);

    private final int bitMask;

    V1_12_EntityPose(int bitMask) {
        this.bitMask = bitMask;
    }

    public int getBitMask() {
        return this.bitMask;
    }

    public static int getBitMasks(V1_12_EntityPose... poses) {
        int bitMask = 0;
        for (V1_12_EntityPose pose : poses) {
            bitMask |= pose.getBitMask();
        }
        return bitMask;
    }
}
