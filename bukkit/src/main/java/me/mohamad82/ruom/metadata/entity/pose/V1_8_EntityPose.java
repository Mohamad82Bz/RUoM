package me.mohamad82.ruom.metadata.entity.pose;

public enum V1_8_EntityPose {
    STANDING(0),
    ON_FIRE(0x01),
    CROUCHING(0x02),
    SPRINTING(0x08),
    EATING_DRINKING_BLOCKING(0x10),
    INVISIBLE(0x20);

    private final int bitMask;

    V1_8_EntityPose(int bitMask) {
        this.bitMask = bitMask;
    }

    public int getBitMask() {
        return this.bitMask;
    }

    public static int getBitMasks(V1_8_EntityPose... poses) {
        int bitMask = 0;
        for (V1_8_EntityPose pose : poses) {
            bitMask |= pose.getBitMask();
        }
        return bitMask;
    }
}
