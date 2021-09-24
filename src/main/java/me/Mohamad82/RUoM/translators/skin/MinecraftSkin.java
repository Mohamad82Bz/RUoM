package me.Mohamad82.RUoM.translators.skin;

import com.cryptomorin.xseries.ReflectionUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.Mohamad82.RUoM.utils.ExperienceUtils;
import net.skinsrestorer.api.PlayerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

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
                Class<?> CRAFT_PLAYER = ReflectionUtils.getCraftClass("entity.CraftPlayer");

                ItemStack[] inventoryContents = player.getInventory().getContents();
                Location location = player.getLocation();
                boolean isHealthScaled = player.isHealthScaled();
                double health = player.getHealth();
                int food = player.getFoodLevel();
                int experience = ExperienceUtils.getTotalExperience(player);
                Entity vehicle = player.getVehicle();

                PropertyMap propertyMap = ((GameProfile) CRAFT_PLAYER.getMethod("getProfile").invoke(player)).getProperties();
                propertyMap.removeAll("textures");
                propertyMap.put("textures", new Property("textures", texture, signature));

                Object entityPlayer = CRAFT_PLAYER.getMethod("getHandle").invoke(player);
                Object removePlayerPacket = getPacketPlayOutPlayerInfo(entityPlayer, "REMOVE_PLAYER");

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    ReflectionUtils.sendPacket(onlinePlayer, removePlayerPacket);
                }

                player.getInventory().clear();
                ExperienceUtils.setTotalExperience(player, 0);

                SkinBuilderListeners.disableDeathMessage = true;
                player.setHealth(0);
                SkinBuilderListeners.disableDeathMessage = false;

                if (vehicle != null)
                    vehicle.removePassenger(player);

                player.spigot().respawn();

                player.setHealthScaled(isHealthScaled);
                player.setHealth(health);
                player.setFoodLevel(food);
                player.teleport(location);
                player.getInventory().setContents(inventoryContents);
                ExperienceUtils.setTotalExperience(player, experience);
                if (vehicle != null)
                    vehicle.addPassenger(player);

                Object addPlayerPacket = getPacketPlayOutPlayerInfo(entityPlayer, "ADD_PLAYER");
                Object namedEntitySpawnPacket = getPacketPlayOutNamedEntitySpawn(entityPlayer);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    ReflectionUtils.sendPacket(onlinePlayer, addPlayerPacket);
                    if (!player.equals(onlinePlayer))
                        ReflectionUtils.sendPacket(onlinePlayer, namedEntitySpawnPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Object getPacketPlayOutPlayerInfo(Object entityPlayer, String action) {
        try {
            Class<?> ENTITY_PLAYER = ReflectionUtils.getNMSClass("server.level", "EntityPlayer");
            Class<?> ENUM_PLAYER_INFO_ACTION = ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            Class<?> PACKET_PLAY_OUT_PLAYER_INFO = ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutPlayerInfo");

            Object addPlayerEnum = ENUM_PLAYER_INFO_ACTION.getField(action.toUpperCase()).get(null);
            Object entityPlayerArray = Array.newInstance(ENTITY_PLAYER, 1);
            Constructor<?> packetPlayerInfoConstructor = PACKET_PLAY_OUT_PLAYER_INFO
                    .getConstructor(ENUM_PLAYER_INFO_ACTION,
                            entityPlayerArray.getClass());
            Array.set(entityPlayerArray, 0, entityPlayer);

            return packetPlayerInfoConstructor.newInstance(addPlayerEnum, entityPlayerArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    private Object getPacketPlayOutNamedEntitySpawn(Object entityPlayer) {
        try {
            Class<?> PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN = ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutNamedEntitySpawn");
            Class<?> ENTITY_HUMAN = ReflectionUtils.getNMSClass("world.entity.player", "EntityHuman");

            Constructor<?> packetNamedEntitySpawnConstructor = PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN
                    .getConstructor(ENTITY_HUMAN);

            return packetNamedEntitySpawnConstructor.newInstance(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public String getTexture() {
        return texture;
    }

    public String getSignature() {
        return signature;
    }

}
