package me.mohamad82.ruom.npc;

public enum NPCAnimation {
    SWING_MAIN_ARM(0),
    TAKE_DAMAGE(1),
    LEAVE_BED(2),
    SWING_OFFHAND(3),
    CRITICAL_EFFECT(4),
    MAGIC_CRITICAL_EFFECT(5);

    private final int value;

    NPCAnimation(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
