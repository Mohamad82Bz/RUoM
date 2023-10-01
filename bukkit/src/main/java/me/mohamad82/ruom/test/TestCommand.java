package me.mohamad82.ruom.test;

import com.cryptomorin.xseries.XMaterial;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.adventure.ComponentUtils;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.npc.NPC;
import me.mohamad82.ruom.npc.PlayerNPC;
import me.mohamad82.ruom.npc.TablistComponent;
import me.mohamad82.ruom.npc.entity.ArmorStandNPC;
import me.mohamad82.ruom.skin.MinecraftSkin;
import me.mohamad82.ruom.skin.SkinBuilder;
import me.mohamad82.ruom.skin.exceptions.NoSuchAccountNameException;
import me.mohamad82.ruom.toast.ToastMessage;
import me.mohamad82.ruom.utils.BlockUtils;
import me.mohamad82.ruom.utils.ListUtils;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class TestCommand implements CommandExecutor {

    PlayerNPC npc = null;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("No argument.");
        } else {
            switch (args[0].toLowerCase()) {
                case "p": {
                    Player player = (Player) sender;
                    BlockUtils.spawnBlockBreakParticles(player.getTargetBlock(null, 5).getLocation(), Material.GRASS);

                    break;
                }
                case "npc": {
                    npc = PlayerNPC.playerNPC("test", ((sender instanceof Player) ? ((Player) sender).getLocation() : new Location(Bukkit.getWorlds().get(0), 0, 0, 0)), Optional.of(
                            new MinecraftSkin(
                                    "ewogICJ0aW1lc3RhbXAiIDogMTY4MjQzOTEzMjQ1OCwKICAicHJvZmlsZUlkIiA6ICI0NmY3N2NjNmQ2MjU0NjEzYjc2NmYyZDRmMDM2MzZhNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaXNzV29sZiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lM2QyZThkZGU0Yzc2ZDJkMDVhNTgwOTQ0NjMwYTdjYWRmYzE2YThiZjkwNWY1NDRiNGRmZDRiN2VjMjFlZTU5IgogICAgfQogIH0KfQ==",
                                    "lEa0S+JWwUBL516ewtRM976YKVkDSLSHVqAb/9nF/G3y7eJ7vOJgynrdlif2kYT+F9ei0QomIa9qVXxjh26p0CFdZoOgomTKz/UHUJ8iSR1WkfO7BcVn50cXq41ZKCi0FV2juduUuJXWqaaKAnQ2oWEJpyQ1wLEUvItMBjbRg/PshbWRXdiq25xqtqwGNspqJ8WH5cgEPocwPaX5H+QEulFtqtCrLLU9y2QpIsxfAEgBhqClBu5UbHtdPR4nz0l3/Qw7s9lTQXM16c14PLE1zAlHkBoigxRp8ZMH5zlS5IPlvp3u2OG4SqVOLsMx6HBJES99ef09s2qSmV5+5HYkrpxDxOWUC7qm7+xrGFakPTGb/ojQdZLRIolzcEZywtomuqoCy1m2jZxu07jDT8cpFnDCX5qyr21LX7TQJV0UFCj514HJvGsMqF4xNXuaVqeT6oY3JUZVuIRvts5y8O2qbkcK20kNn00gk3HLAzFclu7pngXAywPFgxvd5FLsuOyuQ/L2VBzAaKRMR87ws90oKlf0hfZglWGRcnvAJ88JvFKcdQUrt3Z/qsAAPG+tobJN/aS9AE52a+z+P0A46XVnmcKRNzC66iamR+8mJUFwxhEkA8f5uVRGEX3vf5PVDz//HwtVFtvApfVz8ze+a0ZviVbYw0+EmHEIRUOnaEGbqFY="
                            )
                    ));
                    npc.addViewers(((Player) sender));
                    Ruom.runSync(() -> {
                        npc.setModelParts(PlayerNPC.ModelPart.values());
                    }, Integer.parseInt(args[1]));
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
