package me.mohamad82.ruom.toast;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.adventure.AdventureApi;
import me.mohamad82.ruom.adventure.ComponentUtils;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.utils.GsonUtils;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;

public class ToastMessage {

    private final char ignoreChar = 'ˑ';

    private Object addPacket;
    private Object removePacket;
    private Object advancementProgress;

    private ToastMessage(String title, XMaterial icon, FrameType frameType, boolean trimCharacters) {
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

        Ruom.run(() -> {
            Object advancementResourceLocation = NMSUtils.createResourceLocation("ruom_toasts_" + UUID.randomUUID());
            Object advancementBuilder;
            if (ServerVersion.supports(13)) {
                Object deserializationContext;
                if (ServerVersion.supports(20)) {
                    deserializationContext = DeserializationContextAccessor.getConstructor1().newInstance(advancementResourceLocation, LootDataManagerAccessor.getConstructor0().newInstance());
                } else {
                    deserializationContext = DeserializationContextAccessor.getConstructor0().newInstance(advancementResourceLocation, PredicateManagerAccessor.getConstructor0().newInstance());
                }
                advancementBuilder = Advancement_i_BuilderAccessor.getMethodFromJson1().invoke(null, jsonAdvancement, deserializationContext);
            } else {
                advancementBuilder = GsonHelperAccessor.getMethodFromJson1().invoke(null, ServerAdvancementManagerAccessor.getFieldGSON().get(null), GsonUtils.get().toJson(jsonAdvancement), Advancement_i_BuilderAccessor.getType());
            }
            Object advancement = Advancement_i_BuilderAccessor.getMethodBuild1().invoke(advancementBuilder, advancementResourceLocation);

            this.advancementProgress = AdvancementProgressAccessor.getConstructor0().newInstance();
            AdvancementProgressAccessor.getMethodUpdate1().invoke(advancementProgress,
                    AdvancementAccessor.getMethodGetCriteria1().invoke(advancement),
                    AdvancementAccessor.getMethodGetRequirements1().invoke(advancement)
            );

            Collection<Object> toAddSet = new HashSet<>();
            toAddSet.add(advancement);
            Map<Object, Object> progressMap = new HashMap<>();
            progressMap.put(advancementResourceLocation, advancementProgress);
            Set<Object> toRemoveSet = new HashSet<>();
            toRemoveSet.add(advancementResourceLocation);

            addPacket = ClientboundUpdateAdvancementsPacketAccessor.getConstructor0().newInstance(false, toAddSet, Collections.emptySet(), progressMap);
            removePacket = ClientboundUpdateAdvancementsPacketAccessor.getConstructor0().newInstance(false, Collections.emptySet(), toRemoveSet, Collections.emptyMap());
        });
    }

    public static ToastMessage toastMessage(String title, XMaterial icon, FrameType frameType, boolean trimCharacters) {
        return new ToastMessage(title, icon, frameType, trimCharacters);
    }

    public void send(Player... players) {
        awardCriteria();
        Set<Player> playersSet = new HashSet<>(Arrays.asList(players));

        Ruom.runEAsync(() -> {
            NMSUtils.sendPacketSync(playersSet, addPacket);
            Ruom.runAsync(() -> {
                revokeCriteria();
                NMSUtils.sendPacketSync(playersSet, removePacket);
            }, 5);
        });
    }

    private void awardCriteria() {
        Ruom.run(() -> AdvancementProgressAccessor.getMethodGrantProgress1().invoke(advancementProgress, "elytra"));
    }

    private void revokeCriteria() {
        Ruom.run(() -> AdvancementProgressAccessor.getMethodRevokeProgress1().invoke(advancementProgress, "elytra"));
    }

    private JsonElement parseTitle(String rawTitle, boolean trimCharacters) {
        if (trimCharacters)
            rawTitle = trimCharacters(rawTitle);

        Component component = ComponentUtils.parse(rawTitle);
        return GsonUtils.getParser().parse(GsonComponentSerializer.gson().serialize(component));
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

    private String replaceTokensWithIgnoreChar(final String richMessage) {
        final StringBuilder sb = new StringBuilder();
        final Matcher matcher = AdventureApi.escapeTokenPattern.matcher(richMessage);
        int lastEnd = 0;
        int i = 0;
        while (matcher.find()) {
            i++;
            if (i > 20) {
                break;
            }
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
