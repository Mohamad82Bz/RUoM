package me.mohamad82.ruom.metadata.player;

public enum V1_8_PlayerMeta {
    SKIN_MODELS(10),
    ABSORPTION_HEALTH(17),
    SCORE(18);

    private final int index;

    private V1_8_PlayerMeta(final int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
