package me.Mohamad82.RUoM.Vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultLoader {

    private final JavaPlugin plugin;

    public VaultLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private static Economy economy;
    private static Permission permission;
    private static Chat chat;

    public boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        permission = rsp.getProvider();
        return permission != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Permission getPermissions() {
        return permission;
    }

    public Chat getChat() {
        return chat;
    }




}
