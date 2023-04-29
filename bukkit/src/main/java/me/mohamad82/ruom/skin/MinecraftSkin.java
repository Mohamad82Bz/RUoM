package me.mohamad82.ruom.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.PlayerAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import net.skinsrestorer.api.PlayerWrapper;
import org.bukkit.entity.Player;

public class MinecraftSkin {

    private final String texture;
    private final String signature;

    public MinecraftSkin(String texture, String signature) {
        this.texture = texture;
        this.signature = signature;
    }

    public void apply(Player player) {
        if (SkinBuilder.getInstance().getSkinsRestorerAPI() != null) {
            SkinBuilder.getInstance().getSkinsRestorerAPI().applySkin(
                    new PlayerWrapper(player),
                    SkinBuilder.getInstance().getMojangAPI().createProperty("textures", texture, signature)
            );
        } else {
            try {
                Object serverPlayer = NMSUtils.getServerPlayer(player);
                GameProfile gameProfile = (GameProfile) PlayerAccessor.getMethodGetGameProfile1().invoke(serverPlayer);
                gameProfile.getProperties().removeAll("textures");
                gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

                NMSUtils.sendPacket(Ruom.getOnlinePlayers(),
                        PacketUtils.getPlayerInfoPacket(serverPlayer, PacketUtils.PlayerInfoAction.ADD_PLAYER),
                        PacketUtils.getAddEntityPacket(serverPlayer));
                NMSUtils.sendPacket(player,
                        PacketUtils.getRespawnPacket(NMSUtils.getServerLevel(player.getWorld()), player.getGameMode(), player.getGameMode(), false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void apply(Object serverPlayer) throws Exception {
        GameProfile gameProfile = (GameProfile) PlayerAccessor.getMethodGetGameProfile1().invoke(serverPlayer);
        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
    }

    public String getTexture() {
        return texture;
    }

    public String getSignature() {
        return signature;
    }

}
