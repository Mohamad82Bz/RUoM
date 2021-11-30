package me.Mohamad82.RUoM.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XMaterial;
import com.google.gson.*;
import me.Mohamad82.RUoM.Ruom;
import me.Mohamad82.RUoM.adventureapi.AdventureAPIManager;
import me.Mohamad82.RUoM.adventureapi.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

public class ToastMessage {

    private final char ignoreChar = 'ˑ';
    private final Gson gson = new GsonBuilder().create();
    private final JsonParser jsonParser = new JsonParser();

    private Object packet;
    private Object removePacket;
    private Object advancementProgress;

    private static Object DESERIALIZER;

    private static Class<?> MINECRAFT_KEY, ADVANCEMENT, SERIALIZED_ADVANCEMENT, LOOT_DESERIALIZATION_CONTEXT, LOOT_PREDICATE_MANAGER, ADVANCEMENT_PROGRESS,
    PACKET_PLAY_OUT_ADVANCEMENTS, CRAFT_ADVANCEMENT_PROGRESS, CHAT_DESERIALIZER;

    private static Constructor<?> MINECRAFT_KEY_CONSTRUCTOR, LOOT_DESERIALIZATION_CONTEXT_CONSTRUCTOR, LOOT_PREDICATE_MANAGER_CONSTRUCTOR, ADVANCEMENT_PROGRESS_CONSTRUCTOR,
    PACKET_PLAY_OUT_ADVANCEMENTS_CONSTRUCTOR;

    private static Method ADVANCEMENT_SERIALIZE_METHOD, ADVANCEMENT_DESERIALIZE_METHOD, ADVANCEMENT_PROGRESS_UPDATE_METHOD, CHAT_DESERIALIZER_METHOD,
    ADVANCEMENT_PROGRESS_GRANT_CRITERIA_METHOD, ADVANCEMENT_PROGRESS_REVOKE_CRITERIA_METHOD, ADVANCEMENT_GET_CRITERIA_METHOD, ADVANCEMENT_GET_REQUIREMENTS_METHOD;

    private static Field CRAFT_ADVANCEMENT_PROGRESS_HANDLE_FIELD;

    static {
        try {
            {
                if (ServerVersion.getVersion() == 12) {
                    DESERIALIZER = ReflectionUtils.getNMSClass("AdvancementDataWorld").getField("DESERIALIZER").get(null);
                }
            }
            {
                MINECRAFT_KEY = ReflectionUtils.getNMSClass("resources", "MinecraftKey");
                ADVANCEMENT = ReflectionUtils.getNMSClass("advancements", "Advancement");
                SERIALIZED_ADVANCEMENT = ReflectionUtils.getNMSClass("advancements", "Advancement$SerializedAdvancement");
                ADVANCEMENT_PROGRESS = ReflectionUtils.getNMSClass("advancements", "AdvancementProgress");
                PACKET_PLAY_OUT_ADVANCEMENTS = ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutAdvancements");
                CRAFT_ADVANCEMENT_PROGRESS = ReflectionUtils.getCraftClass("advancement.CraftAdvancementProgress");
                if (ServerVersion.supports(13)) {
                    LOOT_DESERIALIZATION_CONTEXT = ReflectionUtils.getNMSClass("advancements.critereon", "LootDeserializationContext");
                    LOOT_PREDICATE_MANAGER = ReflectionUtils.getNMSClass("world.level.storage.loot", "LootPredicateManager");
                } else {
                    CHAT_DESERIALIZER = ReflectionUtils.getNMSClass("ChatDeserializer");
                }
            }
            {
                MINECRAFT_KEY_CONSTRUCTOR = MINECRAFT_KEY.getConstructor(String.class);
                ADVANCEMENT_PROGRESS_CONSTRUCTOR = ADVANCEMENT_PROGRESS.getConstructor();
                PACKET_PLAY_OUT_ADVANCEMENTS_CONSTRUCTOR = PACKET_PLAY_OUT_ADVANCEMENTS.getConstructor(boolean.class, Collection.class, Set.class, Map.class);
                if (ServerVersion.supports(13)) {
                    LOOT_DESERIALIZATION_CONTEXT_CONSTRUCTOR = LOOT_DESERIALIZATION_CONTEXT.getConstructor(MINECRAFT_KEY, LOOT_PREDICATE_MANAGER);
                    LOOT_PREDICATE_MANAGER_CONSTRUCTOR = LOOT_PREDICATE_MANAGER.getConstructor();
                }
            }
            {
                if (ServerVersion.supports(13)) {
                    ADVANCEMENT_SERIALIZE_METHOD = SERIALIZED_ADVANCEMENT.getMethod("a", JsonObject.class, LOOT_DESERIALIZATION_CONTEXT);
                } else {
                    CHAT_DESERIALIZER_METHOD = CHAT_DESERIALIZER.getMethod("a", DESERIALIZER.getClass(), String.class, Class.class);
                }
                ADVANCEMENT_DESERIALIZE_METHOD = SERIALIZED_ADVANCEMENT.getMethod(ServerVersion.supports(13) ? "b" : "a", MINECRAFT_KEY);
                ADVANCEMENT_PROGRESS_UPDATE_METHOD = ADVANCEMENT_PROGRESS.getMethod("a", Map.class, String[][].class);
                ADVANCEMENT_PROGRESS_GRANT_CRITERIA_METHOD = ADVANCEMENT_PROGRESS.getMethod("a", String.class);
                ADVANCEMENT_PROGRESS_REVOKE_CRITERIA_METHOD = ADVANCEMENT_PROGRESS.getMethod("b", String.class);
                ADVANCEMENT_GET_CRITERIA_METHOD = ADVANCEMENT.getMethod("getCriteria");
                ADVANCEMENT_GET_REQUIREMENTS_METHOD = ADVANCEMENT.getMethod("i");
            }
            {
                CRAFT_ADVANCEMENT_PROGRESS_HANDLE_FIELD = CRAFT_ADVANCEMENT_PROGRESS.getDeclaredField("handle");
                CRAFT_ADVANCEMENT_PROGRESS_HANDLE_FIELD.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ToastMessage create(String title, XMaterial icon, FrameType frameType, boolean trimCharacters) {
        return new ToastMessage(title, icon, frameType, trimCharacters);
    }

    protected ToastMessage(String title, XMaterial icon, FrameType frameType, boolean trimCharacters) {
        JsonObject jsonAdvancement = new JsonObject();

        JsonObject displayJson = new JsonObject();
        JsonObject iconJson = new JsonObject();
        JsonObject descJson = new JsonObject();

        JsonObject criteriaJson = new JsonObject();
        JsonObject elytraJson = new JsonObject();

        JsonArray requirementsArray = new JsonArray();
        JsonArray elytraRequirementArray = new JsonArray();

        iconJson.addProperty("item", "minecraft:" + icon.parseMaterial().toString().toLowerCase());
        if (!ServerVersion.supports(13))
            iconJson.addProperty("data", icon.getData());
        descJson.addProperty("text", "");
        displayJson.add("title", parseTitle(title, trimCharacters));
        displayJson.add("icon", iconJson);
        displayJson.add("description", descJson);
        displayJson.addProperty("frame", frameType.toString().toLowerCase());
        displayJson.addProperty("show_toast", true);
        displayJson.addProperty("announce_to_chat", false);
        displayJson.addProperty("hidden", false);

        elytraJson.addProperty("trigger", "minecraft:impossible");
        criteriaJson.add("elytra", elytraJson);

        elytraRequirementArray.add("elytra");
        requirementsArray.add(elytraRequirementArray);

        jsonAdvancement.add("display", displayJson);
        jsonAdvancement.add("criteria", criteriaJson);
        jsonAdvancement.add("requirements", requirementsArray);

        try {
            Object advancementKey = MINECRAFT_KEY_CONSTRUCTOR.newInstance("ruom_toasts" + UUID.randomUUID());
            Object nmsSerializedAdvancement;
            if (ServerVersion.supports(13)) {
                nmsSerializedAdvancement = ADVANCEMENT_SERIALIZE_METHOD.invoke(null, jsonAdvancement, LOOT_DESERIALIZATION_CONTEXT_CONSTRUCTOR.newInstance(advancementKey, LOOT_PREDICATE_MANAGER_CONSTRUCTOR.newInstance()));
            } else {
                nmsSerializedAdvancement = CHAT_DESERIALIZER_METHOD.invoke(null, DESERIALIZER, gson.toJson(jsonAdvancement), SERIALIZED_ADVANCEMENT);
            }
            Object nmsAdvancement = ADVANCEMENT_DESERIALIZE_METHOD.invoke(nmsSerializedAdvancement, advancementKey);
            Object criteriaObject = ADVANCEMENT_GET_CRITERIA_METHOD.invoke(nmsAdvancement);
            Object requirementObject = ADVANCEMENT_GET_REQUIREMENTS_METHOD.invoke(nmsAdvancement);
            this.advancementProgress = ADVANCEMENT_PROGRESS_CONSTRUCTOR.newInstance();

            ADVANCEMENT_PROGRESS_UPDATE_METHOD.invoke(advancementProgress, criteriaObject, requirementObject);

            Collection<Object> toAddSet = new HashSet<>();
            toAddSet.add(nmsAdvancement);
            Map<Object, Object> advancementMap = new HashMap<>();
            advancementMap.put(advancementKey, advancementProgress);
            Set<Object> toRemoveSet = new HashSet<>();
            toRemoveSet.add(advancementKey);

            packet = PACKET_PLAY_OUT_ADVANCEMENTS_CONSTRUCTOR.newInstance(false, toAddSet, Collections.EMPTY_SET, advancementMap);
            removePacket = PACKET_PLAY_OUT_ADVANCEMENTS_CONSTRUCTOR.newInstance(false, Collections.EMPTY_SET, toRemoveSet, Collections.EMPTY_MAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Player... players) {
        awardCriteria();
        for (Player player : players) {
            ReflectionUtils.sendPacket(player, packet);
        }

        Ruom.runSync(() -> {
            revokeCriteria();
            Arrays.stream(players).forEach(player -> {
                try {
                    ReflectionUtils.sendPacket(player, removePacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }, 2);
    }

    private void awardCriteria() {
        try {
            ADVANCEMENT_PROGRESS_GRANT_CRITERIA_METHOD.invoke(advancementProgress, "elytra");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void revokeCriteria() {
        try {
            ADVANCEMENT_PROGRESS_REVOKE_CRITERIA_METHOD.invoke(advancementProgress, "elytra");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonElement parseTitle(String rawTitle, boolean trimCharacters) {
        if (trimCharacters)
            rawTitle = trimCharacters(rawTitle);

        Component component = ComponentUtils.parse(rawTitle);
        return jsonParser.parse(GsonComponentSerializer.gson().serialize(component));
    }

    private String trimCharacters(String input) {
        final int characterLimit = 45;

        input = input.replace(String.valueOf(ignoreChar), ".");
        String modifiedInput = replaceTokensWithIgnoreChar(input);
        int i = 0;
        int j = 0;
        for (char character : modifiedInput.toCharArray()) {
            j++;
            if (character != ignoreChar)
                i++;
            if (i > characterLimit)
                break;
        }
        input = input.substring(0, j);
        if (i > characterLimit)
            input = input + "...";

        return input;
    }

    @SuppressWarnings("SuspiciousRegexArgument")
    private String replaceTokensWithIgnoreChar(final String richMessage) {
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

    public enum FrameType {
        CHALLENGE,
        GOAL,
        TASK
    }

}
