package me.mohamad82.ruom.npc;

import com.mojang.authlib.GameProfile;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.PlayerAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerPlayerAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerPlayerGameModeAccessor;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.skin.MinecraftSkin;
import me.mohamad82.ruom.utils.ListUtils;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

public class PlayerNPC extends LivingEntityNPC {

    private static Field listNameField;

    private final String name;
    private final String tabName16 = "[NPC] " + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    private final float yaw;
    private boolean collision = true;

    protected PlayerNPC(String name, Location location, Optional<MinecraftSkin> skin) {
        super(createServerPlayerObject(name, location.getWorld(), skin), location, NPCType.PLAYER);
        this.name = name;
        this.yaw = location.getYaw();
        if (skin.isPresent())
            setModelParts(ModelPart.values());
    }

    public static Object createServerPlayerObject(String name, World world, Optional<MinecraftSkin> skin) {
        try {
            Object serverLevel = NMSUtils.getServerLevel(world);
            GameProfile profile = new GameProfile(UUID.randomUUID(), name);
            Object entity;
            if (ServerVersion.getVersion() == 19 && ServerVersion.getPatchNumber() < 2) {
                entity = ServerPlayerAccessor.getConstructor2().newInstance(
                        NMSUtils.getDedicatedServer(),
                        serverLevel,
                        profile,
                        null
                );
            } else if (ServerVersion.supports(17)) {
                entity = ServerPlayerAccessor.getConstructor0().newInstance(
                        NMSUtils.getDedicatedServer(),
                        serverLevel,
                        profile
                );
            } else {
                entity = ServerPlayerAccessor.getConstructor1().newInstance(
                        NMSUtils.getDedicatedServer(),
                        serverLevel,
                        profile,
                        ServerVersion.supports(14) ?
                                ServerPlayerGameModeAccessor.getConstructor0().newInstance(serverLevel) :
                                ServerPlayerGameModeAccessor.getConstructor1().newInstance(serverLevel)
                );
            }
            if (skin.isPresent()) {
                skin.get().apply(entity);
            }

            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PlayerNPC playerNPC(String name, Location location, Optional<MinecraftSkin> skin) {
        return new PlayerNPC(name, location, skin);
    }

    public String getName() {
        return name;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
        NMSUtils.sendPacket(getViewers(), createModifyPlayerTeamPacket());
    }

    public void setModelParts(ModelPart... modelParts) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), PlayerAccessor.getFieldDATA_PLAYER_MODE_CUSTOMISATION().get(null), ModelPart.getMasks(modelParts)));
        sendEntityData();
    }

    public void setTabList(@Nullable Component component) {
        NMSUtils.sendPacket(getViewers(),
                PacketUtils.getPlayerInfoPacket(entity, PacketUtils.PlayerInfoAction.REMOVE_PLAYER));
        if (component != null) {
            Ruom.run(() -> listNameField.set(entity, MinecraftComponentSerializer.get().serialize(component)));
            NMSUtils.sendPacket(getViewers(),
                    PacketUtils.getPlayerInfoPacket(entity, PacketUtils.PlayerInfoAction.ADD_PLAYER));
        }
    }

    @Override
    protected void addViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getPlayerInfoPacket(entity, PacketUtils.PlayerInfoAction.ADD_PLAYER),
                PacketUtils.getAddPlayerPacket(entity),
                PacketUtils.getHeadRotatePacket(entity, this.yaw),
                createPlayerTeamPacket()
                );
    }

    @Override
    protected void removeViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getPlayerInfoPacket(entity, PacketUtils.PlayerInfoAction.REMOVE_PLAYER),
                PacketUtils.getRemoveEntitiesPacket(id));
    }

    private Object createPlayerTeamPacket() {
        return PacketUtils.getTeamCreatePacket(
                tabName16,
                Component.empty(),
                Component.empty(),
                PacketUtils.NameTagVisibility.NEVER,
                collision ? PacketUtils.CollisionRule.ALWAYS : PacketUtils.CollisionRule.NEVER,
                ChatColor.BLUE,
                ListUtils.toList(name),
                false
        );
    }

    private Object createModifyPlayerTeamPacket() {
        return PacketUtils.getTeamModifyPacket(
                tabName16,
                Component.empty(),
                Component.empty(),
                PacketUtils.NameTagVisibility.NEVER,
                collision ? PacketUtils.CollisionRule.ALWAYS : PacketUtils.CollisionRule.NEVER,
                ChatColor.BLUE,
                false
        );
    }

    static {
        try {
            listNameField = ServerPlayerAccessor.getType().getField("listName");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum ModelPart {
        CAPE(0x01),
        JACKET(0x02),
        LEFT_SLEEVE(0x04),
        RIGHT_SLEEVE(0x08),
        LEFT_PANTS(0x10),
        RIGHT_PANTS(0x20),
        HAT(0x40);

        private final byte mask;

        ModelPart(int mask) {
            this.mask = (byte) mask;
        }

        public byte getMask() {
            return mask;
        }

        public static byte getMasks(ModelPart... parts) {
            byte bytes = 0;
            for (ModelPart part : parts) {
                bytes += part.getMask();
            }
            return bytes;
        }

        public static byte getAllBitMasks() {
            byte bytes = 0;
            for (ModelPart modelPart : values()) {
                bytes += modelPart.getMask();
            }
            return bytes;
        }
    }

}
