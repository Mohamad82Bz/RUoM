package me.Mohamad82.RUoM.adventureapi;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.markdown.DiscordFlavor;
import org.bukkit.entity.Player;

public class ComponentUtils {

    public static void send(Player player, Component component) {
        AdventureAPIManager.getAdventure().player(player).sendMessage(component);
    }

    public static Component parse(String string) {
        return MiniMessage.get().parse(string);
    }

    public static Component parseD(String string) {
        return MiniMessage.withMarkdownFlavor(DiscordFlavor.get()).parse(string);
    }

}
