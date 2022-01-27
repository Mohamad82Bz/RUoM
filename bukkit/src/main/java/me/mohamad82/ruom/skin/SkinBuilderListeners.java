package me.mohamad82.ruom.skin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class SkinBuilderListeners implements Listener {

    protected static boolean disableDeathMessage = false;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (disableDeathMessage) {
            event.setDeathMessage(null);
        }
    }

}
