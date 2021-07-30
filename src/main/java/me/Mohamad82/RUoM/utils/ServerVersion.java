package me.Mohamad82.RUoM.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import org.bukkit.Bukkit;

public class ServerVersion {

    public static String getVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    public static boolean is1_8() {
        return getVersion().equals("1.8");
    }

    public static boolean isLegacy() {
        String version = getVersion();
        return version.equals("1.8") || version.equals("1.9") || version.equals("1.10") || version.equals("1.11") || version.equals("1.12");
    }

    public static boolean supports(int version) {
        return ReflectionUtils.supports(version);
    }

}
