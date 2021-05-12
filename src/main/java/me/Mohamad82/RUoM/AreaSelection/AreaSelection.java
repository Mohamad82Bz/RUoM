package me.Mohamad82.RUoM.AreaSelection;

import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AreaSelection {

    protected final Player player;
    protected final ItemStack wand;

    private BlockArrayClipboard clipboard;
    private Location firstPos, secondPos;

    public AreaSelection(Player player, ItemStack wand) {
        this.player = player;
        this.wand = wand;
    }

    public abstract void onFirstPos(Player player, boolean updated);

    public abstract void onSecondPos(Player player, boolean updated);

    public abstract void onFirstPosNotify(Player player);

    public abstract void onSecondPosNotify(Player player);

    public Player getPlayer() {
        return player;
    }

    public Location getFirstPos() {
        return firstPos;
    }

    public void setFirstPos(Location firstPos) {
        this.firstPos = firstPos;
    }

    public Location getSecondPos() {
        return secondPos;
    }

    public void setSecondPos(Location secondPos) {
        this.secondPos = secondPos;
    }

}
