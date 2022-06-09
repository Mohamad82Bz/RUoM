package me.mohamad82.ruom.metadata.livingentity;

public enum V1_12_UseItemMeta {
    NONE(0),
    MAIN_HAND(0x01),
    OFF_HAND(0x02);

    private int bitMap;

    V1_12_UseItemMeta(int bitMap) {
        this.bitMap = bitMap;
    }

    public int getBitMap() {
        return this.bitMap;
    }
}
