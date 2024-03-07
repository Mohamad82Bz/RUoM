package me.mohamad82.ruom.npc;

import com.mojang.authlib.GameProfile;
import io.netty.channel.*;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.*;
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
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.SocketAddress;
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
            Object serverPlayer;
            if (ServerVersion.supports(21) || (ServerVersion.getVersion() == 20 && ServerVersion.getPatchNumber() >= 2)) {
                serverPlayer = ServerPlayerAccessor.getConstructor3().newInstance(
                        NMSUtils.getDedicatedServer(),
                        serverLevel,
                        profile,
                        ClientInformationAccessor.getMethodCreateDefault1().invoke(null)
                );
                Object connection = NMSUtils.createConnection();
                ConnectionAccessor.getFieldAddress().set(connection, new SocketAddress() {
                    private static final long serialVersionUID = 8207338859896320185L;
                });

                ConnectionAccessor.getFieldChannel().set(connection, new AbstractChannel(null) {
                    private final ChannelConfig config = new DefaultChannelConfig(this);
                    @Override
                    protected AbstractUnsafe newUnsafe() {
                        return null;
                    }

                    @Override
                    protected boolean isCompatible(EventLoop eventLoop) {
                        return false;
                    }

                    @Override
                    protected SocketAddress localAddress0() {
                        return null;
                    }

                    @Override
                    protected SocketAddress remoteAddress0() {
                        return null;
                    }

                    @Override
                    protected void doBind(SocketAddress socketAddress) throws Exception {

                    }

                    @Override
                    protected void doDisconnect() throws Exception {

                    }

                    @Override
                    protected void doClose() throws Exception {

                    }

                    @Override
                    protected void doBeginRead() throws Exception {

                    }

                    @Override
                    protected void doWrite(ChannelOutboundBuffer channelOutboundBuffer) throws Exception {

                    }

                    @Override
                    public ChannelConfig config() {
                        config.setAutoRead(true);
                        return config;
                    }

                    @Override
                    public boolean isOpen() {
                        return false;
                    }

                    @Override
                    public boolean isActive() {
                        return false;
                    }

                    @Override
                    public ChannelMetadata metadata() {
                        return new ChannelMetadata(true);
                    }
                });
                ConnectionAccessor.getFieldAddress().set(connection, new SocketAddress() {
                    private static final long serialVersionUID = 8207338859896320185L;
                });

                /*Object serverGamePacketImpl = ServerGamePacketListenerImplAccessor.getConstructor0().newInstance(
                        NMSUtils.getDedicatedServer(),
                        connection,
                        serverPlayer,
                        CommonListenerCookieAccessor.getConstructor0().newInstance(
                                profile,
                                0,
                                ClientInformationAccessor.getMethodCreateDefault1().invoke(null)
                        )
                );*/

                ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
                Constructor<?> objectConstructor = Object.class.getDeclaredConstructor();
                Constructor<?> constructor = reflectionFactory.newConstructorForSerialization(ServerGamePacketListenerImplAccessor.getType(), objectConstructor);
                Object instance = ServerGamePacketListenerImplAccessor.getType().cast(constructor.newInstance());

                ServerPlayerAccessor.getFieldConnection().set(
                        serverPlayer, instance
                );
            } else if (ServerVersion.getVersion() == 19 && ServerVersion.getPatchNumber() < 2) {
                serverPlayer = ServerPlayerAccessor.getConstructor2().newInstance(
                        NMSUtils.getDedicatedServer(),
                        serverLevel,
                        profile,
                        null
                );
            } else if (ServerVersion.supports(17)) {
                serverPlayer = ServerPlayerAccessor.getConstructor0().newInstance(
                        NMSUtils.getDedicatedServer(),
                        serverLevel,
                        profile
                );
            } else {
                serverPlayer = ServerPlayerAccessor.getConstructor1().newInstance(
                        NMSUtils.getDedicatedServer(),
                        serverLevel,
                        profile,
                        ServerVersion.supports(14) ?
                                ServerPlayerGameModeAccessor.getConstructor0().newInstance(serverLevel) :
                                ServerPlayerGameModeAccessor.getConstructor1().newInstance(serverLevel)
                );
            }
            if (skin.isPresent()) {
                skin.get().apply(serverPlayer);
            }

            return serverPlayer;
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
                (ServerVersion.supports(21) || (ServerVersion.getVersion() == 20 && ServerVersion.getPatchNumber() >= 3)) ?
                        PacketUtils.getAddEntityPacket(entity) : PacketUtils.getAddPlayerPacket(entity),
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
