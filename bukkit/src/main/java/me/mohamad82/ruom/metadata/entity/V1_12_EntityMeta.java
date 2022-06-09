package me.mohamad82.ruom.metadata.entity;

import me.mohamad82.ruom.metadata.entity.pose.V1_12_EntityPose;

public enum V1_12_EntityMeta {
    /**
     * @see V1_12_EntityPose
     */
    POSE(0),
    /**
     * int, 0-300
     */
    AIR_TIME(1),
    /**
     * @apiNote String
     */
    CUSTOM_NAME(2),
    CUSTOM_NAME_VISIBILITY(3),
    IS_SILENT(4),
    NO_GRAVITY(5);

    private final int index;

    private V1_12_EntityMeta(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
