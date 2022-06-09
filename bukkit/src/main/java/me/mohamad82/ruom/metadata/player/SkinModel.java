package me.mohamad82.ruom.metadata.player;

public enum SkinModel {
    CAPE(0x01),
    JACKET(0x02),
    LEFT_SLEEVE(0x04),
    RIGHT_SLEEVE(0x08),
    LEFT_PANTS(0x10),
    RIGHT_PANTS(0x20),
    HAT(0x40);

    private final byte bitMask;

    SkinModel(int mask) {
        this.bitMask = (byte) mask;
    }

    public byte getBitMask() {
        return bitMask;
    }

    public static byte getBitMasks(SkinModel... parts) {
        byte bytes = 0;
        for (SkinModel part : parts) {
            bytes += part.getBitMask();
        }
        return bytes;
    }

    public static byte getAllBitMasks() {
        byte bytes = 0;
        for (SkinModel playerSkin : SkinModel.values()) {
            bytes += playerSkin.getBitMask();
        }
        return bytes;
    }
}
