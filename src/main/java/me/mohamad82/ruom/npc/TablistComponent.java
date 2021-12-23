package me.mohamad82.ruom.npc;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.ServerPlayerAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Optional;

public class TablistComponent extends PlayerNPC {

    private static Field listNameField;

    private TablistComponent(Component component, String string) {
        super(string, new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Optional.empty());

        Ruom.run(() -> listNameField.set(entity, MinecraftComponentSerializer.get().serialize(component)));
    }

    /**
     * A tablist component that can be added to certain players.
     * @param component The component that is going to be added in tablist.
     * @param string The string that will be used for tablist order.
     */
    public static TablistComponent tablistComponent(Component component, String string) {
        return new TablistComponent(component, string);
    }

    public void setComponent(Component component) {
        Ruom.run(() -> listNameField.set(entity, MinecraftComponentSerializer.get().serialize(component)));

        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getPlayerInfoPacket(entity, "ADD_PLAYER"));
    }

    public Component getComponent() {
        try {
            return MinecraftComponentSerializer.get().deserialize(listNameField.get(entity));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void addViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getPlayerInfoPacket(entity, "ADD_PLAYER"));
    }

    @Override
    protected void removeViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getPlayerInfoPacket(entity, "REMOVE_PLAYER"));
    }

    static {
        try {
            listNameField = ServerPlayerAccessor.getType().getField("listName");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
