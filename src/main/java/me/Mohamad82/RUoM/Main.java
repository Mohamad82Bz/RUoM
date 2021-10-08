package me.Mohamad82.RUoM;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.GsonBuilder;
import me.Mohamad82.RUoM.adventureapi.AdventureAPIManager;
import me.Mohamad82.RUoM.adventureapi.ComponentUtils;
import me.Mohamad82.RUoM.translators.skin.MineSkinAPI;
import me.Mohamad82.RUoM.translators.skin.MinecraftSkin;
import me.Mohamad82.RUoM.translators.skin.SkinBuilder;
import me.Mohamad82.RUoM.utils.MilliCounter;
import me.Mohamad82.RUoM.utils.ToastMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Main extends RUoMPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("ruom").setExecutor(this);
        new SkinBuilder();
        Ruom.initializeAdventure();
    }

    @Override
    public void onDisable() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MilliCounter counter = new MilliCounter();
        counter.start();
        try {
            if (args[0].equalsIgnoreCase("toast")) {
                ToastMessage toastMessage = ToastMessage.create(args[1], XMaterial.valueOf(args[2].toUpperCase()), ToastMessage.FrameType.valueOf(args[3].toUpperCase()), Boolean.parseBoolean(args[4]));
                toastMessage.send((Player) sender);
            } else if (args[0].equalsIgnoreCase("esc")) {
                Component component = ComponentUtils.parseD(args[1]);
                AdventureAPIManager.getAdventure().player((Player) sender).sendMessage(component);
                Bukkit.broadcastMessage(new GsonBuilder().setPrettyPrinting().create().toJson(GsonComponentSerializer.gson().serialize(component)));
            } else if (args[0].equalsIgnoreCase("esc2")) {
                Component component = ComponentUtils.parse(args[1]);
                AdventureAPIManager.getAdventure().player((Player) sender).sendMessage(component);
                Bukkit.broadcastMessage(new GsonBuilder().setPrettyPrinting().create().toJson(GsonComponentSerializer.gson().serialize(component)));
            } else if (args[0].equalsIgnoreCase("mojang")) {
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
