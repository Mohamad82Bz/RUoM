package me.Mohamad82.RUoM.translators.skin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class SkinBuilderListeners implements Listener {

    public static boolean disableDeathMessage = false;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (disableDeathMessage) {
            event.setDeathMessage(null);
        }
    }

}
