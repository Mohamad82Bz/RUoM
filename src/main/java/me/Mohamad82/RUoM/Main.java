package me.Mohamad82.RUoM;

import me.Mohamad82.RUoM.translators.skin.MineSkinAPI;
import me.Mohamad82.RUoM.translators.skin.MinecraftSkin;
import me.Mohamad82.RUoM.translators.skin.SkinBuilder;
import me.Mohamad82.RUoM.utils.MilliCounter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Main extends RUoMPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("ruom").setExecutor(this);
        new SkinBuilder();
    }

    @Override
    public void onDisable() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MilliCounter counter = new MilliCounter();
        counter.start();
        try {
            if (args[0].equalsIgnoreCase("mojang")) {
                MinecraftSkin skin = SkinBuilder.getInstance().getSkin(args[1], true);
                skin.apply((Player) sender);

            } else if (args[0].equalsIgnoreCase("mineskin")) {
                MinecraftSkin skin = SkinBuilder.getInstance().getSkin(args[1], MineSkinAPI.SkinType.NORMAL, true);
                skin.apply((Player) sender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        counter.stop();
        sender.sendMessage("Took " + counter.get() + "ms");

        return false;
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
