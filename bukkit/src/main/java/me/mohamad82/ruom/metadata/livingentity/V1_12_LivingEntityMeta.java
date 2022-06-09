package me.mohamad82.ruom.metadata.livingentity;

public enum V1_12_LivingEntityMeta {
    USE_ITEM(6),
    /**
     * @apiNote float
     */
    HEALTH(7),
    /**
     * @apiNote int, 0 if there is no effect
     */
    POTION_EFFECT_COLOR(8),
    /**
     * @apiNote boolean
     */
    IS_POTION_EFFECT_AMBIENT(9),
    /**
     * @apiNote int
     */
    BODY_ARROWS(10);

    private final int index;

    V1_12_LivingEntityMeta(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
