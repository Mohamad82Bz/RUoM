package me.Mohamad82.RUoM.AreaSelection;

import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AreaSelection {

    protected final Player player;
    protected final ItemStack wand;
    protected BlockArrayClipboard clipboard;
    protected Location firstPos, secondPos;

    public AreaSelection(Player player, ItemStack wand) {
        this.player = player;
        this.wand = wand;
    }

    public AreaSelection(ItemStack wand) {
        this(null, wand);
    }

    public abstract void onFirstPos(Player player, boolean updated);

    public abstract void onSecondPos(Player player, boolean updated);

    public abstract void onFirstPosNotify(Player player);

    public abstract void onSecondPosNotify(Player player);

    public Player getPlayer() {
        return player;
    }

    public BlockArrayClipboard getClipboard() {
        return clipboard;
    }

    public void setClipboard(BlockArrayClipboard clipboard) {
        this.clipboard = clipboard;
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
