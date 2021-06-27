package me.Mohamad82.RUoM;

import org.bukkit.plugin.java.JavaPlugin;

public final class RUoM extends JavaPlugin {

    public static JavaPlugin instance;

    @Override
    public void onEnable() {
        initialize(this);

        /*getCommand("ruom").setExecutor(this);*/
    }

    @Override
    public void onDisable() {

    }

    public static void initialize(JavaPlugin plugin) {
        instance = plugin;
    }

    public static JavaPlugin getInstance() {
        return instance;
    }

    /*public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("ruom")) {
            WEManager weManager = new WEManager(this, WEType.FAWE);

            SchemProgress progress = weManager.buildSchematic(Bukkit.getPlayerExact("Mohamad82").getLocation(), new File(getDataFolder(),
                    "test.schem"), PastePattern.valueOf(args[0]), PasteSpeed.valueOf(args[1]), Boolean.parseBoolean(args[2]));

            new BukkitRunnable() {
                public void run() {
                    Bukkit.broadcastMessage("Progress: " + progress.getProgress());
                    if (progress.isDone()) {
                        cancel();
                    }
                }
            }.runTaskTimer(this, 20, 20);
        }
        return true;
    }*/

}
