package me.Mohamad82.RUoM.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XMaterial;
import com.google.gson.*;
import me.Mohamad82.RUoM.Ruom;
import me.Mohamad82.RUoM.adventureapi.AdventureAPIManager;
import me.Mohamad82.RUoM.adventureapi.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.UnsafeValues;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;

@SuppressWarnings("deprecation")
public class ToastMessage {

    private final static char ignoreChar = 'ˑ';

    private String title;
    private final NamespacedKey id;
    private final Advancement advancement;

    protected ToastMessage(String title, XMaterial icon, FrameType frameType, boolean trimCharacters) {
        this.title = title;
        id = NamespacedKey.fromString("ruom_toasts/" + UUID.randomUUID());

        String iconId;
        if (ServerVersion.supports(13)) {
            iconId = "minecraft:" + icon.parseMaterial().toString().toLowerCase();
        } else {
            iconId = "material:" + icon.getId();
        }

        JsonObject json = new JsonObject();
        JsonObject iconObject = new JsonObject();
        iconObject.addProperty("item", iconId);
        if (!ServerVersion.supports(13)) {
            iconObject.addProperty("data", icon.getData());
        }

        JsonObject displayObject = new JsonObject();
        displayObject.add("icon", iconObject);

        try {
            Class.forName("net.kyori.adventure.text.Component");

            if (trimCharacters) {
                title = title.replace(String.valueOf(ignoreChar), ".");
                String escapedTitle = replaceTokensWithIgnoreChar(title);
                int i = 0;
                int j = 0;
                for (char character : escapedTitle.toCharArray()) {
                    j++;
                    if (character != ignoreChar)
                        i++;
                    if (i > 40)
                        break;
                }
                title = title.substring(0, j);
                if (i > 40)
                    title = title + "...";
            }

            Component component = ComponentUtils.parseD(title);
            displayObject.add("title", parseJsonString(GsonComponentSerializer.gson().serialize(component)));
        } catch (ClassNotFoundException e2) {
            trimTitleCharacters();
            displayObject.addProperty("title", title);
        }
        displayObject.addProperty("description", "");
        displayObject.addProperty("frame", frameType.toString().toLowerCase());
        displayObject.addProperty("announce_to_chat", false);
        displayObject.addProperty("show_toast", true);
        displayObject.addProperty("hidden", true);

        JsonObject criteriaObject = new JsonObject();
        JsonObject conditionsObject = new JsonObject();
        JsonObject elytra = new JsonObject();
        JsonArray itemArray = new JsonArray();

        conditionsObject.add("items", itemArray);
        elytra.addProperty("trigger", "minecraft:impossible");
        elytra.add("conditions", conditionsObject);
        criteriaObject.add("elytra", elytra);

        json.add("criteria", criteriaObject);
        json.add("display", displayObject);

        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(json);

        for (World world : Bukkit.getWorlds()) {
            Path path = Paths.get(world.getWorldFolder() + File.separator + "data"
                    + File.separator + "advancements" + File.separator + id.getNamespace() + File.separator + id.getKey().split("/")[0]);
            Path path2 = Paths.get(world.getWorldFolder() + File.separator + "data"
                    + File.separator + "advancements" + File.separator + id.getNamespace()
                    + File.separator + id.getKey().split("/")[0] + File.separator + id.getKey().split("/")[1] + ".json");

            if (!path.toFile().exists()){
                path.toFile().mkdirs();
            }

            if (!path2.toFile().exists()){
                File file = path2.toFile();
                try {
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.write(jsonString);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Bukkit.getUnsafe().loadAdvancement(id, jsonString);
        this.advancement = Bukkit.getAdvancement(id);
    }

    @SuppressWarnings("SuspiciousRegexArgument")
    private static String replaceTokensWithIgnoreChar(final String richMessage) {
        final StringBuilder sb = new StringBuilder();
        final Matcher matcher = AdventureAPIManager.escapeTokenPattern.matcher(richMessage);
        int lastEnd = 0;
        while (matcher.find()) {
            final int startIndex = matcher.start();
            final int endIndex = matcher.end();

            if (startIndex > lastEnd) {
                sb.append(richMessage, lastEnd, startIndex);
            }
            lastEnd = endIndex;

            String token = matcher.group("token");
            final String inner = matcher.group("inner");

            // also escape inner
            if (inner != null) {
                token = token.replace(inner, replaceTokensWithIgnoreChar(inner));
            }

            sb.append(ignoreChar).append(token.replaceAll(".", String.valueOf(ignoreChar))).append(ignoreChar);
        }

        if (richMessage.length() > lastEnd) {
            sb.append(richMessage.substring(lastEnd));
        }

        return sb.toString();
    }

    /**
     * Creates a ToastMessage that can be sent to players.
     * @param title Title of the message
     * @param icon Icon of the message
     * @param frameType Frame type of the message
     * @param trimCharacters Total of 2 lines can fit in the toast messages' frames which is ~40 characters, Set this to true if you want to limit the characters
     * @return A ready-to-send toast message
     */
    public static ToastMessage create(String title, XMaterial icon, FrameType frameType, boolean trimCharacters) {
        return new ToastMessage(title, icon, frameType, trimCharacters);
    }

    public static ToastMessage create(String title, XMaterial icon, FrameType frameType) {
        return new ToastMessage(title, icon, frameType, true);
    }

    public void send(Player... players) {
        for (Player player : players) {
            if (!player.getAdvancementProgress(advancement).isDone()) {
                player.getAdvancementProgress(advancement).awardCriteria("elytra");
                Bukkit.getScheduler().runTaskLater(Ruom.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        player.getAdvancementProgress(advancement).revokeCriteria("elytra");
                    }
                }, 5);
            }
        }
    }

    public void sendAndDelete(Player... players) {
        send(players);
        delete();
    }

    public void delete() {
        Bukkit.getScheduler().runTaskLater(Ruom.getPlugin(), new Runnable() {
            @Override
            public void run() {
                try {
                    Class<?> CRAFT_MAGIC_NUMBERS = ReflectionUtils.getCraftClass("util.CraftMagicNumbers");
                    UnsafeValues unsafeValues = (UnsafeValues) CRAFT_MAGIC_NUMBERS.getField("INSTANCE").get(null);
                    unsafeValues.removeAdvancement(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 5);
    }

    private JsonElement parseJsonString(String jsonString) {
        return new JsonParser().parse(jsonString);
    }

    private void trimTitleCharacters() {
        if (title.length() > 40) {
            title = title.substring(0, 40);
        }
    }

    public enum FrameType {
        CHALLENGE,
        GOAL,
        TASK
    }

}
