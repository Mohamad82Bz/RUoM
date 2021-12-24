package me.mohamad82.ruom.gui;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.AbstractContainerMenuAccessor;
import me.mohamad82.ruom.nmsaccessors.PlayerAccessor;
import me.mohamad82.ruom.nmsaccessors.ServerPlayerAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GUITitle {

    public static void setTitle(Player player, Component component) {
        Ruom.run(() -> {
            if (player.getOpenInventory().getTopInventory() == null) return;
            Inventory inventory = player.getOpenInventory().getTopInventory();
            if (inventory.getSize() % 9 != 0) return;

            Object serverPlayer = NMSUtils.getServerPlayer(player);
            Object containerMenu = PlayerAccessor.getFieldContainerMenu().get(serverPlayer);
            int containerId = (int) AbstractContainerMenuAccessor.getFieldContainerId().get(containerMenu);

            NMSUtils.sendPacket(player, PacketUtils.getOpenScreenPacket(containerId, inventory.getSize(), component));

            if (ServerVersion.supports(17)) {
                AbstractContainerMenuAccessor.getMethodSendAllDataToRemote1().invoke(containerMenu);
            } else {
                ServerPlayerAccessor.getMethodRefreshContainer1().invoke(serverPlayer, containerMenu);
            }
        });
    }

}