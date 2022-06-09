package me.mohamad82.ruom.metadata.entity;

import me.mohamad82.ruom.metadata.entity.pose.V1_8_EntityPose;

public enum V1_8_EntityMeta {
    /**
     * @see V1_8_EntityPose
     */
    POSE(0),
    AIR_TIME(1),
    /**
     * @apiNote String
     */
    CUSTOM_NAME(2),
    /**
     * @apiNote boolean
     */
    CUSTOM_NAME_VISIBILITY(3),
    /**
     * @apiNote boolean
     */
    IS_SILENT(4),
    /**
     * @apiNote boolean
     */
    NO_GRAVITY(5);

    private final int index;

    private V1_8_EntityMeta(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
