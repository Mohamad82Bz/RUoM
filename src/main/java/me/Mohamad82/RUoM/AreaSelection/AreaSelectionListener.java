package me.Mohamad82.RUoM.AreaSelection;

import me.Mohamad82.RUoM.Firework.InstantFirework;
import me.Mohamad82.RUoM.LocUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AreaSelectionListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        AreaSelection areaSelection = AreaSelectionManager.getInstance().getPlayerAreaSection(player);
        if (areaSelection == null) return;

        ItemStack wand = AreaSelectionManager.getInstance().wand;

        //TODO VERSIONSUP: MC-1.8 doesn't have Main Hand, add support for it.
        if (!(event.getPlayer().getInventory().getItemInMainHand().equals(wand))) return;

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
                InstantFirework.explode(LocUtils.simplifyLocationToCenter(areaSelection.getFirstPos()), Color.BLUE, Color.AQUA,
                        FireworkEffect.Type.BALL_LARGE, false, false);
                areaSelection.onFirstPosNotify(player);
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (areaSelection.getSecondPos() != null) {
                InstantFirework.explode(LocUtils.simplifyLocationToCenter(areaSelection.getSecondPos()), Color.RED, Color.ORANGE,
                        FireworkEffect.Type.BALL_LARGE, false, false);
                areaSelection.onSecondPosNotify(player);
            }
        }
    }

}
