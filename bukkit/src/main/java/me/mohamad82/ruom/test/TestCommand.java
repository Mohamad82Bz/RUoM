package me.mohamad82.ruom.test;

import com.cryptomorin.xseries.XMaterial;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.math.vector.Vector3;
import me.mohamad82.ruom.npc.NPC;
import me.mohamad82.ruom.npc.PlayerNPC;
import me.mohamad82.ruom.npc.entity.ArmorStandNPC;
import me.mohamad82.ruom.utils.ListUtils;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("No argument.");
        } else {
            switch (args[0].toLowerCase()) {
                case "packetutils": {
                    Ruom.broadcast(test_packetUtils() + "");
                    break;
                }
                case "nmsutils": {
                    Ruom.broadcast(test_nmsUtils((Player) sender) + "");
                    break;
                }
            }
        }
        return true;
    }

    public boolean test_packetUtils() {
        assert PacketUtils.getOpenScreenPacket(0, 9, Component.empty()) != null;
        assert PacketUtils.getRespawnPacket(NMSUtils.getServerLevel(Bukkit.getWorlds().get(0)), GameMode.SURVIVAL, GameMode.SURVIVAL, true, true) != null;
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
