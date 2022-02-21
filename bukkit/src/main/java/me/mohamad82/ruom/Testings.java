package me.mohamad82.ruom;

import me.mohamad82.ruom.adventure.ComponentUtils;
import me.mohamad82.ruom.utils.NMSUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Testings extends RUoMPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("ruom").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("ruom")) {
            Player player = (Player) sender;
            switch (args[0].toLowerCase()) {
                case "sign": {
                    Sign sign = (Sign) player.getTargetBlock(null, 5).getState();
                    NMSUtils.setSignLine(sign, Integer.parseInt(args[1]), ComponentUtils.parse(args[2]));
                    NMSUtils.updateSign(sign);

                    ComponentUtils.send(player, NMSUtils.getSignLine(sign, Integer.parseInt(args[1])));
                    player.sendMessage("-------");
                    for (Component component : NMSUtils.getSignLines(sign)) {
                        ComponentUtils.send(player, component);
                    }
                    break;
                }
            }
            return true;
        }
        return false;
    }

}
