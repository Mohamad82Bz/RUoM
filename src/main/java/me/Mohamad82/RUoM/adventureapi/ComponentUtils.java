package me.Mohamad82.RUoM.adventureapi;

import me.Mohamad82.RUoM.Ruom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class ComponentUtils {

    static {
        Ruom.initializeAdventure();
    }

    public static void send(Player player, Component component) {
        AdventureAPIManager.getAdventure().player(player).sendMessage(component);
    }

    public static void send(Component component, Player... players) {
        for (Player player : players) {
            send(player, component);
        }
    }

    public static Component parse(String string) {
        return MiniMessage.miniMessage().parse(parseComponentColors(string));
    }

    public static String parseComponentColors(String msg) {
        return msg.replace("&0", "<reset><black>")
                .replace("&1", "<reset><dark_blue>")
                .replace("&2", "<reset><dark_green>")
                .replace("&3", "<reset>dark_blue")
                .replace("&4", "<reset><dark_red>")
                .replace("&5", "<reset><dark_purple>")
                .replace("&6", "<reset><gold>")
                .replace("&7", "<reset><gray>")
                .replace("&8", "<reset><dark_gray>")
                .replace("&9", "<reset><blue>")
                .replace("&a", "<reset><green>")
                .replace("&b", "<reset><aqua>")
                .replace("&c", "<reset><red>")
                .replace("&d", "<reset><light_purple>")
                .replace("&e", "<reset><yellow>")
                .replace("&f", "<reset><white>")
                .replace("&l", "<bold>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>")
                .replace("&k", "<obfuscated>");
    }

}
