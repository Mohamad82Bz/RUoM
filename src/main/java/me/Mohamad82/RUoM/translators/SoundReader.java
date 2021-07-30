package me.Mohamad82.RUoM.translators;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Logger;

public class SoundReader {

    private final Logger logger;

    public SoundReader(Logger logger) {
        this.logger = logger;
    }

    public SoundReader() {
        this.logger = null;
    }

    public void play(Player player, String rawSound) {
        if (rawSound.contains(" ; ") || rawSound.contains(" : ")) {
            //Sound is formatted
            String[] sound = null;
            if (rawSound.contains(" ; ")) sound = rawSound.split(" ; ");
            if (rawSound.contains(" : ")) sound = rawSound.split(" : ");
            try {
                XSound.valueOf(sound[0]).play(player, Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
            } catch (Exception e) {
                logger.warning(String.format("Could not read '%s' sound because it's formatted wrongly" +
                        " or sound is invalid!", rawSound.toUpperCase()));
            }
        } else {
            //Sound is not formatted
            try {
                XSound.valueOf(rawSound).play(player);
            } catch (IllegalArgumentException e) {
                logger.warning(String.format("Could not read '%s' sound because the sound is invalid!",
                        rawSound.toUpperCase()));
            }

        }
    }

    public void play(List<Player> players, String rawSound) {
        for (Player player : players) {
            play(player, rawSound);
        }
    }

}
