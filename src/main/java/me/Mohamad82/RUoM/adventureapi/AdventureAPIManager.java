package me.Mohamad82.RUoM.adventureapi;

import me.Mohamad82.RUoM.Ruom;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

public class AdventureAPIManager {

    private static BukkitAudiences adventure;

    public static BukkitAudiences getAdventure() {
        return adventure;
    }

    public static void initialize() {
        adventure = BukkitAudiences.create(Ruom.getPlugin());
    }

}
