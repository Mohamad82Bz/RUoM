package me.mohamad82.ruom.skin;

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
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty;
import net.skinsrestorer.shared.utils.connections.MojangAPI;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SkinBuilder {

    private final Map<String, MinecraftSkin> cache = new HashMap<>();

    private SkinsRestorerAPI skinsRestorerAPI;
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
            skinsRestorerAPI = SkinsRestorerAPI.getApi();

            try {
                Field mojangAPIField = skinsRestorerAPI.getClass().getField("mojangAPI");
                mojangAPIField.setAccessible(true);
                mojangAPI = (MojangAPI) mojangAPIField.get(skinsRestorerAPI);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public MinecraftSkin getSkin(Player player) {
        try {
            GameProfile gameProfile = (GameProfile) PlayerAccessor.getMethodGetGameProfile1().invoke(NMSUtils.getServerPlayer(player));
            Property property = gameProfile.getProperties().get("textures").iterator().next();

            return new MinecraftSkin(property.getValue(), property.getSignature());
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

        String name = skinsRestorerAPI.getSkinName(playerName);
        if (name == null)
            throw new NoSuchAccountNameException();
        IProperty property = skinsRestorerAPI.getSkinData(name);

        return new MinecraftSkin(property.getValue(), property.getSignature());
    }

    public SkinsRestorerAPI getSkinsRestorerAPI() {
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
