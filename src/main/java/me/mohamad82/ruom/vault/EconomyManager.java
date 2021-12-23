package me.mohamad82.ruom.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

public class EconomyManager {

    private final Economy economy;

    public EconomyManager(Economy economy) {
        this.economy = economy;
    }

    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return economy.depositPlayer(player, amount);
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return economy.withdrawPlayer(player, amount);
    }

}
