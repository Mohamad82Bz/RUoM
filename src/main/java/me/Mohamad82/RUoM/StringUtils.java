package me.Mohamad82.RUoM;

import org.bukkit.ChatColor;

public class StringUtils {

    public static String capitalize(String string) {
        return string.toUpperCase().charAt(0) + string.toLowerCase().substring(1);
    }

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
