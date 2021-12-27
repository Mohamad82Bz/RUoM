package me.mohamad82.ruom.event;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.utils.MilliCounter;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerUseItemEvent {

    private final BukkitTask worker;

    public PlayerUseItemEvent() {
        worker = new BukkitRunnable() {
            final Map<Player, ItemStack> currentHolds = new HashMap<>();
            final Map<UUID, MilliCounter> currentHoldsTimes = new HashMap<>();
            public void run() {
                for (Player player : Ruom.getOnlinePlayers()) {
                    ItemStack item = NMSUtils.getPlayerUseItem(player);
                    if (item == null) {
                        if (currentHolds.containsKey(player) && currentHoldsTimes.containsKey(player.getUniqueId())) {
                            MilliCounter counter = currentHoldsTimes.get(player.getUniqueId());
                            counter.stop();

                            onStopUseItem(player, item, counter.get());

                            currentHolds.remove(player);
                            currentHoldsTimes.remove(player.getUniqueId());
                        }
                    } else {
                        if (!currentHolds.containsKey(player)) {
                            boolean isMainHand;
                            if (ServerVersion.supports(9)) {
                                if (player.getInventory().getItemInOffHand() != null) {
                                    isMainHand = !player.getInventory().getItemInOffHand().getType().isInteractable();
                                } else {
                                    isMainHand = true;
                                }
                            } else {
                                isMainHand = true;
                            }
                            onStartUseItem(player, item, isMainHand);
                            
                            currentHolds.put(player, item);
                            MilliCounter counter = new MilliCounter();
                            counter.start();
                            currentHoldsTimes.put(player.getUniqueId(), counter);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Ruom.getPlugin(), 0, 1);
    }

    protected abstract void onStartUseItem(Player player, ItemStack item, boolean isMainHand);

    protected abstract void onStopUseItem(Player player, ItemStack item, float holdTime);

    public void unregister() {
        worker.cancel();
    }

}
