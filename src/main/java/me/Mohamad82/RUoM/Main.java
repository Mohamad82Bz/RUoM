package me.Mohamad82.RUoM;

public final class Main extends RUoMPlugin {

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

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
