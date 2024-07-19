package me.mohamad82.ruom.skin;

import com.cryptomorin.xseries.profiles.PlayerProfiles;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.PlayerAccessor;
import me.mohamad82.ruom.skin.exceptions.MineSkinAPIException;
import me.mohamad82.ruom.skin.exceptions.NoSuchAccountNameException;
import me.mohamad82.ruom.skin.exceptions.SkinParseException;
import me.mohamad82.ruom.utils.NMSUtils;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.connections.MojangAPI;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.MojangSkinDataResult;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SkinBuilder {

    private final Map<String, MinecraftSkin> cache = new HashMap<>();

    private SkinsRestorer skinsRestorerAPI;
    private MojangAPI mojangAPI; //SkinsRestorer
    private final MineSkinAPI mineSkinAPI;
    private boolean hasSkinsRestorer = false;

    private static SkinBuilder instance;

    public static SkinBuilder getInstance() {
        if (instance == null) instance = new SkinBuilder();
        return instance;
    }

    public SkinBuilder() {
        instance = this;
        mineSkinAPI = new MineSkinAPI();

        if (Ruom.hasPlugin("SkinsRestorer")) {
            hasSkinsRestorer = true;
            skinsRestorerAPI = SkinsRestorerProvider.get();

            mojangAPI = skinsRestorerAPI.getMojangAPI();
        }

        Ruom.registerListener(new SkinBuilderListeners());
    }

    public MinecraftSkin getSkin(String name, boolean shouldCache) throws NoSuchAccountNameException {
        MinecraftSkin skin;

        if (cache.containsKey(name)) {
            return cache.get(name);
        } else {
            try {
                HttpsURLConnection url = (HttpsURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
                InputStreamReader reader = new InputStreamReader(url.getInputStream());
                String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

                URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                InputStreamReader reader2 = new InputStreamReader(url2.openStream());
                JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

                String texture = property.get("value").getAsString();
                String signature = property.get("signature").getAsString();
                skin = new MinecraftSkin(texture, signature);
                if (shouldCache) {
                    cache.put(name, skin);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new NoSuchAccountNameException();
            }
        }

        return skin;
    }

    public MinecraftSkin getSkin(String url, MineSkinAPI.SkinType skinType, boolean shouldCache) throws MineSkinAPIException, SkinParseException {
        if (cache.containsKey(url)) {
            return cache.get(url);
        } else {
            MinecraftSkin skin = mineSkinAPI.getSkin(url, skinType);
            if (shouldCache) {
                cache.put(url, skin);
            }
            return skin;
        }
    }

    /**
     * Gets the skin of the player.
     * @param player The player.
     * @return The skin of the player. Null if the player has no skin (has default skin).
     */
    public MinecraftSkin getSkin(Player player) {
        try {
            GameProfile gameProfile = (GameProfile) PlayerAccessor.getMethodGetGameProfile1().invoke(NMSUtils.getServerPlayer(player));
            if (!gameProfile.getProperties().containsKey("textures")) {
                return null;
            }
            Property property = gameProfile.getProperties().get("textures").iterator().next();

            return new MinecraftSkin(PlayerProfiles.getSkinValue(gameProfile), property.getSignature());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<MinecraftSkin> getSkinAsync(String name, boolean shouldCache) throws NoSuchAccountNameException {
        CompletableFuture<MinecraftSkin> completableFuture = new CompletableFuture<>();
        AtomicBoolean gotException = new AtomicBoolean(false);

        new BukkitRunnable() {
            public void run() {
                try {
                    completableFuture.complete(getSkin(name, shouldCache));
                } catch (NoSuchAccountNameException e) {
                    gotException.set(true);
                }
            }
        }.runTaskAsynchronously(Ruom.getPlugin());

        if (gotException.get()) {
            throw new NoSuchAccountNameException();
        }

        return completableFuture;
    }

    public CompletableFuture<MinecraftSkin> getSkinAsync(String url, MineSkinAPI.SkinType skinType, boolean shouldCache) throws MineSkinAPIException, SkinParseException {
        CompletableFuture<MinecraftSkin> completableFuture = new CompletableFuture<>();
        AtomicInteger gotException = new AtomicInteger(-1);

        if (cache.containsKey(url)) {
            completableFuture.complete(cache.get(url));
        } else {
            new BukkitRunnable() {
                public void run() {
                    try {
                        MinecraftSkin skin = mineSkinAPI.getSkin(url, skinType);
                        if (shouldCache) {
                            cache.put(url, skin);
                        }
                        completableFuture.complete(skin);
                    } catch (MineSkinAPIException e) {
                        gotException.set(1);
                    } catch (SkinParseException e) {
                        gotException.set(2);
                    }
                }
            }.runTaskAsynchronously(Ruom.getPlugin());

            switch (gotException.get()) {
                case 1:
                    throw new MineSkinAPIException();
                case 2:
                    throw new SkinParseException();
            }
        }

        return completableFuture;
    }

    public MinecraftSkin getSkinFromSkinsRestorer(String playerName) throws NoSuchAccountNameException {
        if (!hasSkinsRestorer) return null;

        MojangSkinDataResult result = null;
        try {
            result = skinsRestorerAPI.getSkinStorage().getPlayerSkin(playerName, true).orElseThrow();
        } catch (DataRequestException | NoSuchElementException e) {
            throw new NoSuchAccountNameException();
        }

        return new MinecraftSkin(result.getSkinProperty().getValue(), result.getSkinProperty().getSignature());
    }

    public SkinsRestorer getSkinsRestorerAPI() {
        return skinsRestorerAPI;
    }

    public MojangAPI getMojangAPI() {
        return mojangAPI;
    }

    public boolean hasSkinsRestorer() {
        return hasSkinsRestorer;
    }

    public void resetCache() {
        cache.clear();
    }

    public Map<String, MinecraftSkin> getCache() {
        return cache;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder stringBuilder = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                stringBuilder.append(chars, 0, read);

            return stringBuilder.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

}
