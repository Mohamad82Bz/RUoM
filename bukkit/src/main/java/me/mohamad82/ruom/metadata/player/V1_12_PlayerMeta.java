package me.mohamad82.ruom.metadata.player;

public enum V1_12_PlayerMeta {
    ABSORPTION_HEALTH(11),
    SCORE(12),
    SKIN_MODELS(13),
    MAIN_HAND(14),
    LEFT_SHOULDER(15),
    RIGHT_SHOULDER(16);

    private final int index;

    private V1_12_PlayerMeta(final int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
