package me.mohamad82.ruom.string;

public class StringUtils {

    public static String capitalize(String string) {
        return string.toUpperCase().charAt(0) + string.toLowerCase().substring(1);
    }

}
