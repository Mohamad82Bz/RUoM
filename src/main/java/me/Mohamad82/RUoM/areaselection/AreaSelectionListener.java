package me.Mohamad82.RUoM.areaselection;

import me.Mohamad82.RUoM.misc.InstantFirework;
import me.Mohamad82.RUoM.utils.LocUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AreaSelectionListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        ItemStack wand = AreaSelectionManager.getInstance().wand;
        if (!(event.getPlayer().getInventory().getItem(EquipmentSlot.HAND).equals(wand))) return;
        if (!(AreaSelectionManager.getInstance().containsPlayerAreaSelection(player))) return;

        AreaSelection areaSelection = AreaSelectionManager.getInstance().getPlayerAreaSection(player);

        event.setCancelled(true);

        boolean isUpdated = false;

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            //First Position Select
            Location blockLoc = event.getClickedBlock().getLocation();
            boolean updated = areaSelection.getFirstPos() == null || !areaSelection.getFirstPos().equals(blockLoc);
            if (updated)
                areaSelection.setFirstPos(event.getClickedBlock().getLocation());
            areaSelection.onFirstPos(player, updated);
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //Second Position Select
            Location blockLoc = event.getClickedBlock().getLocation();
            boolean updated = areaSelection.getSecondPos() == null || !areaSelection.getSecondPos().equals(blockLoc);
            if (updated)
                areaSelection.setSecondPos(event.getClickedBlock().getLocation());
            areaSelection.onSecondPos(player, updated);
        } else if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
            if (areaSelection.getFirstPos() != null) {
                InstantFirework.explode(LocUtils.simplifyToCenter(areaSelection.getFirstPos()), Color.BLUE, Color.AQUA,
                        FireworkEffect.Type.BALL_LARGE, false, false);
                areaSelection.onFirstPosNotify(player);
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (areaSelection.getSecondPos() != null) {
                InstantFirework.explode(LocUtils.simplifyToCenter(areaSelection.getSecondPos()), Color.RED, Color.ORANGE,
                        FireworkEffect.Type.BALL_LARGE, false, false);
                areaSelection.onSecondPosNotify(player);
            }
        }
    }

}