package me.mohamad82.ruom.test;

import com.cryptomorin.xseries.XMaterial;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.adventure.ComponentUtils;
import me.mohamad82.ruom.gui.GUI;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.npc.NPC;
import me.mohamad82.ruom.npc.PlayerNPC;
import me.mohamad82.ruom.npc.TablistComponent;
import me.mohamad82.ruom.npc.entity.ArmorStandNPC;
import me.mohamad82.ruom.scoreboard.Scoreboard;
import me.mohamad82.ruom.skin.MinecraftSkin;
import me.mohamad82.ruom.toast.ToastMessage;
import me.mohamad82.ruom.utils.BlockUtils;
import me.mohamad82.ruom.utils.ListUtils;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class TestCommand implements CommandExecutor {

    PlayerNPC npc = null;

    protected static final String[] COLOR_CODES = Arrays.stream(ChatColor.values())
            .map(Object::toString)
            .toArray(String[]::new);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("No argument.");
        } else {
            switch (args[0].toLowerCase()) {
                case "mount": {
                    Player player = (Player) sender;

                    ArmorStandNPC npc1 = ArmorStandNPC.armorStandNPC(player.getLocation());
                    npc1.addViewers(Ruom.getOnlinePlayers());
                    ArmorStandNPC npc2 = ArmorStandNPC.armorStandNPC(player.getLocation());
                    npc2.addViewers(Ruom.getOnlinePlayers());
                    NMSUtils.setPassengers(Ruom.getOnlinePlayers(), NMSUtils.getServerPlayer(player), npc1.getId());
                    NMSUtils.setPassengers(Ruom.getOnlinePlayers(), npc1.getEntity(), npc2.getId());

                    break;
                }
                case "gamemode": {
                    Player player = (Player) sender;

                    NMSUtils.sendPacket(player, PacketUtils.getUpdateGameModePacket(NMSUtils.getServerPlayer(player), GameMode.SPECTATOR));
                    Ruom.broadcast("sent packet");
                    break;
                }
                case "board": {
                    Player player = (Player) sender;

                    Scoreboard scoreboard = Scoreboard.scoreboard("testboard", ComponentUtils.parse("<gold>UwwU"));
                    scoreboard.setLine(2, ComponentUtils.parse("<red>Fourth Line"));
                    scoreboard.setLine(3, ComponentUtils.parse("<rainbow>5 Line"));
                    scoreboard.setLine(4, ComponentUtils.parse("<red>6 Line"));
                    scoreboard.setLine(5, ComponentUtils.parse("<red>7 Line"));
                    scoreboard.setLine(6, ComponentUtils.parse("<red>8 Line"));
                    scoreboard.setLine(7, ComponentUtils.parse("<red>9 Line"));
                    scoreboard.setLine(13, ComponentUtils.parse("<red>BlaBla Line"));

                    scoreboard.addViewers(player);

                    break;
                }
                case "score": {
                    Player player = (Player) sender;

                    String id = "testboard";
                    int score = 0;

                    Ruom.run(() -> {
                        Object scoreboard = ScoreboardAccessor.getConstructor0().newInstance();
                        ScoreboardAccessor.getMethodAddObjective1().invoke(
                                scoreboard,
                                "test1",
                                ObjectiveCriteriaAccessor.getFieldTRIGGER().get(null),
                                MinecraftComponentSerializer.get().serialize(ComponentUtils.parse("<blue>Title")),
                                ObjectiveCriteria_i_RenderTypeAccessor.getFieldINTEGER().get(null)
                        );
                        Object objective = ScoreboardAccessor.getMethodGetOrCreateObjective1().invoke(scoreboard, "test1");

                        Object packet3 = ClientboundSetScorePacketAccessor.getConstructor0().newInstance(
                                ServerScoreboard_i_MethodAccessor.getFieldCHANGE(),
                                "test1",
                                COLOR_CODES[score],
                                score
                        );

                        Object packet = ClientboundSetObjectivePacketAccessor.getConstructor0().newInstance(
                                objective,
                                0
                        );
                        Object packet2 = ClientboundSetDisplayObjectivePacketAccessor.getConstructor0().newInstance(
                                1,
                                objective
                        );

                        Object packet4 = PacketUtils.getTeamCreatePacket(
                                "test1",
                                ComponentUtils.parse("<gold>prefix"),
                                Component.empty(),
                                PacketUtils.NameTagVisibility.ALWAYS,
                                PacketUtils.CollisionRule.ALWAYS,
                                ChatColor.GRAY,
                                ListUtils.toList(COLOR_CODES[score]),
                                false
                        );

                        NMSUtils.sendPacket(player, packet, packet2, packet3, packet4);

                        player.sendMessage("sent");
                    });
                    break;
                }
                case "p": {
                    Player player = (Player) sender;
                    BlockUtils.spawnBlockBreakParticles(player.getTargetBlock(null, 5).getLocation(), XMaterial.GRASS_BLOCK.parseMaterial());

                    break;
                }
                case "npc": {
                    npc = PlayerNPC.playerNPC("test", ((sender instanceof Player) ? ((Player) sender).getLocation() : new Location(Bukkit.getWorlds().get(0), 0, 0, 0)), Optional.of(
                            new MinecraftSkin(
                                    "ewogICJ0aW1lc3RhbXAiIDogMTY4MjQzOTEzMjQ1OCwKICAicHJvZmlsZUlkIiA6ICI0NmY3N2NjNmQ2MjU0NjEzYjc2NmYyZDRmMDM2MzZhNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaXNzV29sZiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lM2QyZThkZGU0Yzc2ZDJkMDVhNTgwOTQ0NjMwYTdjYWRmYzE2YThiZjkwNWY1NDRiNGRmZDRiN2VjMjFlZTU5IgogICAgfQogIH0KfQ==",
                                    "lEa0S+JWwUBL516ewtRM976YKVkDSLSHVqAb/9nF/G3y7eJ7vOJgynrdlif2kYT+F9ei0QomIa9qVXxjh26p0CFdZoOgomTKz/UHUJ8iSR1WkfO7BcVn50cXq41ZKCi0FV2juduUuJXWqaaKAnQ2oWEJpyQ1wLEUvItMBjbRg/PshbWRXdiq25xqtqwGNspqJ8WH5cgEPocwPaX5H+QEulFtqtCrLLU9y2QpIsxfAEgBhqClBu5UbHtdPR4nz0l3/Qw7s9lTQXM16c14PLE1zAlHkBoigxRp8ZMH5zlS5IPlvp3u2OG4SqVOLsMx6HBJES99ef09s2qSmV5+5HYkrpxDxOWUC7qm7+xrGFakPTGb/ojQdZLRIolzcEZywtomuqoCy1m2jZxu07jDT8cpFnDCX5qyr21LX7TQJV0UFCj514HJvGsMqF4xNXuaVqeT6oY3JUZVuIRvts5y8O2qbkcK20kNn00gk3HLAzFclu7pngXAywPFgxvd5FLsuOyuQ/L2VBzAaKRMR87ws90oKlf0hfZglWGRcnvAJ88JvFKcdQUrt3Z/qsAAPG+tobJN/aS9AE52a+z+P0A46XVnmcKRNzC66iamR+8mJUFwxhEkA8f5uVRGEX3vf5PVDz//HwtVFtvApfVz8ze+a0ZviVbYw0+EmHEIRUOnaEGbqFY="
                            )
                    ));
                    npc.setModelParts(PlayerNPC.ModelPart.values());
                    npc.addViewers(((Player) sender));
                    break;
                }
                case "npc2": {
                    npc = PlayerNPC.playerNPC("test", ((sender instanceof Player) ? ((Player) sender).getLocation() : new Location(Bukkit.getWorlds().get(0), 0, 0, 0)), Optional.of(
                            new MinecraftSkin(
                                    "ewogICJ0aW1lc3RhbXAiIDogMTY4MjQzOTEzMjQ1OCwKICAicHJvZmlsZUlkIiA6ICI0NmY3N2NjNmQ2MjU0NjEzYjc2NmYyZDRmMDM2MzZhNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaXNzV29sZiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lM2QyZThkZGU0Yzc2ZDJkMDVhNTgwOTQ0NjMwYTdjYWRmYzE2YThiZjkwNWY1NDRiNGRmZDRiN2VjMjFlZTU5IgogICAgfQogIH0KfQ==",
                                    "lEa0S+JWwUBL516ewtRM976YKVkDSLSHVqAb/9nF/G3y7eJ7vOJgynrdlif2kYT+F9ei0QomIa9qVXxjh26p0CFdZoOgomTKz/UHUJ8iSR1WkfO7BcVn50cXq41ZKCi0FV2juduUuJXWqaaKAnQ2oWEJpyQ1wLEUvItMBjbRg/PshbWRXdiq25xqtqwGNspqJ8WH5cgEPocwPaX5H+QEulFtqtCrLLU9y2QpIsxfAEgBhqClBu5UbHtdPR4nz0l3/Qw7s9lTQXM16c14PLE1zAlHkBoigxRp8ZMH5zlS5IPlvp3u2OG4SqVOLsMx6HBJES99ef09s2qSmV5+5HYkrpxDxOWUC7qm7+xrGFakPTGb/ojQdZLRIolzcEZywtomuqoCy1m2jZxu07jDT8cpFnDCX5qyr21LX7TQJV0UFCj514HJvGsMqF4xNXuaVqeT6oY3JUZVuIRvts5y8O2qbkcK20kNn00gk3HLAzFclu7pngXAywPFgxvd5FLsuOyuQ/L2VBzAaKRMR87ws90oKlf0hfZglWGRcnvAJ88JvFKcdQUrt3Z/qsAAPG+tobJN/aS9AE52a+z+P0A46XVnmcKRNzC66iamR+8mJUFwxhEkA8f5uVRGEX3vf5PVDz//HwtVFtvApfVz8ze+a0ZviVbYw0+EmHEIRUOnaEGbqFY="
                            )
                    ));
                    npc.addViewers(((Player) sender));
                    npc.setModelParts(PlayerNPC.ModelPart.values());
                    Ruom.runSync(() -> {
                        npc.setTabList(null);
                    }, 10);
                    break;
                }
                case "customnameon": {
                    npc.setCustomNameVisible(Boolean.parseBoolean(args[1]));
                    Ruom.broadcast("set");
                    break;
                }
                case "customname": {
                    NMSUtils.sendPacketSync(
                            ((Player) sender),
                            PacketUtils.getTeamCreatePacket(
                                    UUID.randomUUID().toString().replace("-", "").substring(0, 15),
                                    ComponentUtils.parse("prefix"),
                                    ComponentUtils.parse("suffix"),
                                    PacketUtils.NameTagVisibility.NEVER,
                                    PacketUtils.CollisionRule.NEVER,
                                    ChatColor.BLUE,
                                    ListUtils.toList(
                                            "test3"
                                    ),
                                    false
                            )
                    );
                    /*npc.displayName(ListUtils.toList(
                            org.screamingsandals.lib.spectator.Component.text("first line", Color.AQUA),
                            org.screamingsandals.lib.spectator.Component.text("second line", Color.AQUA),
                            org.screamingsandals.lib.spectator.Component.text("third line", Color.AQUA),
                            org.screamingsandals.lib.spectator.Component.text("fourth line", Color.AQUA)
                    ));*/
                    //npc.setCustomName(ComponentUtils.parse(args[1]));
                    break;
                }
                case "toast": {
                    ToastMessage.toastMessage(args[1], XMaterial.ACACIA_BOAT, ToastMessage.FrameType.TASK, true).send((Player) sender);
                    sender.sendMessage("sent");
                    break;
                }
                case "packetutils": {
                    Ruom.broadcast(test_packetUtils() + "");
                    break;
                }
                case "nmsutils": {
                    Ruom.broadcast(test_nmsUtils((Player) sender) + "");
                    break;
                }
                case "debug1": {
                    TablistComponent tablist = TablistComponent.tablistComponent(ComponentUtils.parse("<rainbow>Testtttt"), "Name111", Optional.empty());
                    tablist.addViewers(Ruom.getOnlinePlayers());
                    break;
                }
                case "debug2": {
                    Player player = (Player) sender;
                    Object packet = PacketUtils.getTeamCreatePacket(
                            "team1",
                            ComponentUtils.parse("<gold>TEAM 1"),
                            ComponentUtils.parse(""),
                            PacketUtils.NameTagVisibility.ALWAYS,
                            PacketUtils.CollisionRule.ALWAYS,
                            ChatColor.BLUE,
                            ListUtils.toList("Name111", "Mohamad82"),
                            true
                    );
                    NMSUtils.sendPacket(player, packet);
                    break;
                }
            }
        }
        return true;
    }

    public boolean test_packetUtils() {
        assert PacketUtils.getOpenScreenPacket(0, 9, Component.empty()) != null;
        assert PacketUtils.getRespawnPacket(NMSUtils.getServerLevel(Bukkit.getWorlds().get(0)), GameMode.SURVIVAL, GameMode.SURVIVAL, true) != null;
        Object serverPlayer = PlayerNPC.createServerPlayerObject("", Bukkit.getWorlds().get(0), Optional.empty());
        assert PacketUtils.getPlayerInfoPacket(serverPlayer, PacketUtils.PlayerInfoAction.ADD_PLAYER) != null;
        assert PacketUtils.getPlayerInfoPacket(serverPlayer, PacketUtils.PlayerInfoAction.REMOVE_PLAYER) != null;
        assert PacketUtils.getAddPlayerPacket(serverPlayer) != null;
        Object entity = ArmorStandNPC.armorStandNPC(new Location(Bukkit.getWorlds().get(0), 0, 0, 0)).getEntity();
        assert PacketUtils.getAddEntityPacket(entity) != null;
        assert PacketUtils.getAddEntityPacket(entity, 1) != null;
        assert PacketUtils.getHeadRotatePacket(entity, 0) != null;
        assert PacketUtils.getRemoveEntitiesPacket(0) != null;
        assert PacketUtils.getEntityRotPacket(0, 0, 0) != null;
        assert PacketUtils.getEntityPosPacket(0, 0, 0, 0) != null;
        assert PacketUtils.getEntityPosRotPacket(0, 0, 0, 0, 0, 0, true) != null;
        assert PacketUtils.getTeleportEntityPacket(entity) != null;
        assert PacketUtils.getEntityVelocityPacket(0, 0, 0, 0) != null;
        assert PacketUtils.getAnimatePacket(entity, 0) != null;
        assert PacketUtils.getBlockDestructionPacket(Vector3.getZero(), 0) != null;
        assert PacketUtils.getEntityEquipmentPacket(0, NPC.EquipmentSlot.HEAD, NMSUtils.getNmsEmptyItemStack()) != null;
        assert PacketUtils.getCollectItemPacket(0, 0, 1) != null;
        assert PacketUtils.getBlockEventPacket(Vector3.getZero(), XMaterial.AIR.parseMaterial(), 0, 0) != null;
        assert PacketUtils.getEntityPassengersPacket(entity, 0) != null;
        assert PacketUtils.getContainerSetContentPacket(0, 1, ListUtils.toList(XMaterial.DIAMOND.parseItem()), XMaterial.DIAMOND.parseItem()) != null;
        assert PacketUtils.getChatPacket(Component.empty(), PacketUtils.ChatType.CHAT, null) != null;
        assert PacketUtils.getPlayerTeamPacket("", Component.empty(), Component.empty(), PacketUtils.NameTagVisibility.NEVER, PacketUtils.CollisionRule.ALWAYS, ChatColor.BLUE, Collections.emptyList(), true, 0) != null;
        assert PacketUtils.getEntityEventPacket(entity, (byte) 17) != null;
        assert PacketUtils.getEntityDataPacket(entity) != null;

        return true;
    }

    public boolean test_nmsUtils(Player player) {
        assert NMSUtils.getNmsItemStack(XMaterial.DIAMOND.parseItem()) != null;
        assert NMSUtils.getNmsEmptyItemStack() != null;
        assert NMSUtils.getBukkitItemStack(NMSUtils.getNmsEmptyItemStack()) != null;
        assert NMSUtils.getItemStackComponent(XMaterial.DIAMOND.parseItem()) != null;
        assert NMSUtils.getItemStackNBTJson(XMaterial.DIAMOND.parseItem()) != null;
        assert NMSUtils.getItemStackFromNBTJson(NMSUtils.getItemStackNBTJson(XMaterial.DIAMOND.parseItem())) != null;
        assert NMSUtils.getItemCategory(XMaterial.DIAMOND.parseItem()) != null;
        assert NMSUtils.getPlayerUseItem(player) != null;

        return true;
    }

}
