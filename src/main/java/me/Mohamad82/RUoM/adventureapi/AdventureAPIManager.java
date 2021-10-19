package me.Mohamad82.RUoM.adventureapi;

import me.Mohamad82.RUoM.Ruom;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.util.regex.Pattern;

public class AdventureAPIManager {

    public static final Pattern escapeTokenPattern = Pattern.compile("((?<start><)(?<token>[^<>]+(:(?<inner>['\"]?([^'\"](\\\\['\"])?)+['\"]?))*)(?<end>>))+?");

    private static BukkitAudiences adventure;

    public static BukkitAudiences getAdventure() {
        return adventure;
    }

    public static void initialize() {
        if (adventure == null)
            adventure = BukkitAudiences.create(Ruom.getPlugin());
    }

}
