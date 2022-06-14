package me.mohamad82.ruom.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundGroup {

    private final Sound breakSound;
    private final Sound placeSound;
    private final Sound stepSound;
    private final Sound hitSound;
    private final Sound fallSound;

    public SoundGroup(Sound breakSound, Sound placeSound, Sound stepSound, Sound hitSound, Sound fallSound) {
        this.breakSound = breakSound;
        this.placeSound = placeSound;
        this.stepSound = stepSound;
        this.hitSound = hitSound;
        this.fallSound = fallSound;
    }

    public Sound getBreakSound() {
        return breakSound;
    }

    public Sound getPlaceSound() {
        return placeSound;
    }

    public Sound getStepSound() {
        return stepSound;
    }

    public Sound getHitSound() {
        return hitSound;
    }

    public Sound getFallSound() {
        return fallSound;
    }

}
