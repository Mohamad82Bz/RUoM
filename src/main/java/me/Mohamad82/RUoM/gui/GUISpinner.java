package me.Mohamad82.RUoM.gui;

import me.Mohamad82.RUoM.gui.exceptions.SpinnerNullSoundReaderException;
import me.Mohamad82.RUoM.translators.SoundReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GUISpinner {

    private final JavaPlugin plugin;
    private final SoundReader soundReader;

    public GUISpinner(JavaPlugin plugin) {
        this.plugin = plugin;
        this.soundReader = null;
    }

    public GUISpinner(JavaPlugin plugin, SoundReader soundReader) {
        this.plugin = plugin;
        this.soundReader = soundReader;
    }

    public CompletableFuture<ItemStack> spin(List<Player> players, List<ItemStack> items, List<Integer> slots, int pointSlot,
                                             int maximumTick, String everyTickSound, String finalTickSound) {
        if (everyTickSound != null && finalTickSound != null)
            if (soundReader == null) {
                throw new SpinnerNullSoundReaderException();
            }
        final CompletableFuture<ItemStack> cf = new CompletableFuture<>();
        new BukkitRunnable() {
            int ticks = 0;
            int delayedTicks = 0;
            float delay = 0;
            int n = 0;
            public void run() {
                ticks++;
                delay += (float) 1 / 100;
                if (ticks > delay * 10) {
                    delayedTicks++;
                    if (delayedTicks > maximumTick) {
                        if (finalTickSound != null)
                            soundReader.play(players, finalTickSound);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 1);
                        cf.complete(players.get(0).getInventory().getItem(pointSlot));

                        cancel();
                        return;
                    }

                    if (everyTickSound != null)
                        soundReader.play(players, everyTickSound);

                    ticks = 0;

                    if (slots.size() == 1) {
                        for (Player player : players) {
                            player.getInventory().setItem(slots.get(0), items.get(n));
                        }
                        n++;
                        if (n >= items.size())
                            n = 0;
                    } else {
                        ItemStack[] item = new ItemStack[slots.size()];
                        for (int j = 0; j < slots.size(); j++) {
                            int i = j;
                            if (i >= items.size()) {
                                i = i - items.size();
                                item[j] = items.get(i);
                                continue;
                            }
                            item[j] = items.get(j);
                        }
                        int itemNum;
                        for (int s = 0; s < slots.size(); s++) {
                            itemNum = s + n;
                            if (itemNum >= item.length) itemNum =- item.length;
                            for (Player player : players) {
                                player.getInventory().setItem(slots.get(s), item[itemNum]);
                            }
                        }

                        n++;
                        if (n >= item.length)
                            n = 0;
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 1);

        return cf;
    }

    public CompletableFuture<ItemStack> spin(List<Player> players, List<ItemStack> items, List<Integer> slots, int pointSlot,
                                             int maximumTick) {
        return spin(players, items, slots, pointSlot, maximumTick, null, null);
    }

    public CompletableFuture<ItemStack> spin(Player player, List<ItemStack> kits, List<Integer> slots, int pointSlot,
                                             int maximumTick, String everyTickSound, String finalTickSound) {
        List<Player> players = new ArrayList<>();
        players.add(player);
        return spin(players, kits, slots, pointSlot, maximumTick, everyTickSound, finalTickSound);
    }

    public CompletableFuture<ItemStack> spin(Player player, List<ItemStack> kits, List<Integer> slots, int pointSlot,
                                             int maximumTick) {
        List<Player> players = new ArrayList<>();
        players.add(player);
        return spin(players, kits, slots, pointSlot, maximumTick, null, null);
    }

}
