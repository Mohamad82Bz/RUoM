package me.mohamad82.ruom.adventure;

import me.mohamad82.ruom.Ruom;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.util.regex.Pattern;

public class Adventure {

    public static final Pattern escapeTokenPattern = Pattern.compile("((?<start><)(?<token>[^<>]+(:(?<inner>['\"]?([^'\"](\\\\['\"])?)+['\"]?))*)(?<end>>))+?");

    private static BukkitAudiences adventure;

    public static BukkitAudiences get() {
        return adventure;
    }

    public static void initialize() {
        if (adventure == null)
            adventure = BukkitAudiences.create(Ruom.getPlugin());
    }

}
