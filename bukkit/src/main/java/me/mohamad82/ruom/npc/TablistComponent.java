package me.mohamad82.ruom.npc;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.ServerPlayerAccessor;
import me.mohamad82.ruom.skin.MinecraftSkin;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Optional;

public class TablistComponent extends PlayerNPC {

    private static Field listNameField;

    private TablistComponent(@Nullable Component component, String string, Optional<MinecraftSkin> skin) {
        super(string, new Location(Bukkit.getWorlds().get(0), 0, 0, 0), skin);

        if (component != null) {
            Ruom.run(() -> listNameField.set(entity, MinecraftComponentSerializer.get().serialize(component)));
        }
    }

    /**
     * A tablist component that can be added to certain players.
     * @param component The component that is going to be added in tablist. If null is given, it will not change the string, but will be able to be changed with team packets.
     * @param string The string that will be used for tablist order.
     */
    public static TablistComponent tablistComponent(@Nullable Component component, String string, Optional<MinecraftSkin> skin) {
        return new TablistComponent(component, string, skin);
    }

    public void setComponent(Component component) {
        Ruom.run(() -> listNameField.set(entity, MinecraftComponentSerializer.get().serialize(component)));

        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getPlayerInfoPacket(entity, PacketUtils.PlayerInfoAction.ADD_PLAYER));
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
                PacketUtils.getPlayerInfoPacket(entity, PacketUtils.PlayerInfoAction.ADD_PLAYER));
    }

    @Override
    protected void removeViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getPlayerInfoPacket(entity, PacketUtils.PlayerInfoAction.REMOVE_PLAYER));
    }

    static {
        try {
            listNameField = ServerPlayerAccessor.getType().getField("listName");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
