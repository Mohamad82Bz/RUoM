package me.Mohamad82.RUoM.utils;

import org.bukkit.ChatColor;

public class StringUtils {

    public static String capitalize(String string) {
        return string.toUpperCase().charAt(0) + string.toLowerCase().substring(1);
    }

    public static String colorize(String string) {
        if (ServerVersion.supports(16))
            string = RGBParser.parse(string);
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}