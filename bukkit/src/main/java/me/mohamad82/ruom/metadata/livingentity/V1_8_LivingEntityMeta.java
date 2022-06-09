package me.mohamad82.ruom.metadata.livingentity;

public enum V1_8_LivingEntityMeta {
    /**
     * @apiNote float
     */
    HEALTH(6),
    /**
     * @apiNote int
     */
    POTION_EFFECT_COLOR(7),
    /**
     * @apiNote byte
     */
    IS_POTION_EFFECT_AMBIENT(8),
    /**
     * @apiNote byte
     */
    BODY_ARROWS(9);

    private final int index;

    V1_8_LivingEntityMeta(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
