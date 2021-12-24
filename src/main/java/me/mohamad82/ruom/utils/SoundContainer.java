package me.mohamad82.ruom.utils;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Set;

public class SoundContainer {

    private final Sound sound;
    private float volume;
    private float pitch;

    private SoundContainer(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static SoundContainer soundContainer(Sound sound, float volume, float pitch) {
        return new SoundContainer(sound, volume, pitch);
    }

    public static SoundContainer soundContainer(Sound sound) {
        return new SoundContainer(sound, 1, 1);
    }

    public static SoundContainer soundContainer(XSound xSound, float volume, float pitch) {
        return new SoundContainer(xSound.parseSound(), volume, pitch);
    }

    public static SoundContainer soundContainer(XSound xSound) {
        return new SoundContainer(xSound.parseSound(), 1, 1);
    }

    public void play(Location location, Player... players) {
        for (Player player : players) {
            player.playSound(location, sound, volume, pitch);
        }
    }

    public void play(Location location, Set<Player> players) {
        play(location, players.toArray(new Player[0]));
    }

    public void play(Player... players) {
        for (Player player : players) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void play(Location location) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public void play(Set<Player> players) {
        play(players.toArray(new Player[0]));
    }

    public SoundContainer withVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public SoundContainer withPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

}
