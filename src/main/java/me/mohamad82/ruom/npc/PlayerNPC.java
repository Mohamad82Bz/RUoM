package me.mohamad82.ruom.npc;

import com.mojang.authlib.GameProfile;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerPlayerAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerPlayerGameModeAccessor;
import me.mohamad82.ruom.translators.skin.MinecraftSkin;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

public class PlayerNPC extends NPC {

    private static Field listNameField;

    private final float yaw;

    protected PlayerNPC(String name, Location location, Optional<MinecraftSkin> skin) {
        this.yaw = location.getYaw();

        Ruom.run(() -> {
            GameProfile profile = new GameProfile(UUID.randomUUID(), name);
            Object entity;
            if (ServerVersion.supports(17)) {
                entity = ServerPlayerAccessor.getConstructor0().newInstance(
                        NMSUtils.getDedicatedServer(),
                        NMSUtils.getServerLevel(location.getWorld()),
                        profile
                );
            } else {
                entity = ServerPlayerAccessor.getConstructor1().newInstance(
                        NMSUtils.getDedicatedServer(),
                        NMSUtils.getServerLevel(location.getWorld()),
                        profile,
                        ServerPlayerGameModeAccessor.getConstructor0().newInstance(NMSUtils.getServerLevel(location.getWorld()))
                );
            }
            EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ());
            initialize(entity);
            if (skin.isPresent())
                skin.get().apply(entity);
        });
    }

    public static PlayerNPC playerNPC(String name, Location location, Optional<MinecraftSkin> skin) {
        return new PlayerNPC(name, location, skin);
    }

    public void collect(int collectedEntityId, int amount) {
        collect(collectedEntityId, id, amount);
    }

    public void setTabList(@Nullable Component component) {
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getPlayerInfoPacket(entity, "REMOVE_PLAYER"));
        if (component != null) {
            Ruom.run(() -> listNameField.set(entity, MinecraftComponentSerializer.get().serialize(component)));
            NMSUtils.sendPacket(getViewers(),
                    PacketUtils.getPlayerInfoPacket(entity, "ADD_PLAYER"));
        }
    }

    @Override
    protected void addViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getPlayerInfoPacket(entity, "ADD_PLAYER"),
                PacketUtils.getAddPlayerPacket(entity),
                PacketUtils.getHeadRotatePacket(entity, this.yaw));
    }

    @Override
    protected void removeViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getPlayerInfoPacket(entity, "REMOVE_PLAYER"),
                PacketUtils.getRemoveEntitiesPacket(id));
    }

    static {
        try {
            listNameField = ServerPlayerAccessor.getType().getField("listName");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
