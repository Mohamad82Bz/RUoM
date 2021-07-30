package me.Mohamad82.RUoM.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RGBParser {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String parse(String message) {
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, "" + ChatColor.of(color));
        }

        return message;
    }

}
