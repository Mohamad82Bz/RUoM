package me.Mohamad82.RUoM.utils;

import me.Mohamad82.RUoM.vector.Vector3;
import me.Mohamad82.RUoM.vector.Vector3Utils;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public static Vector3 getPlayerVector3(Player player) {
        return Vector3Utils.toVector3(player.getLocation());
    }

}
