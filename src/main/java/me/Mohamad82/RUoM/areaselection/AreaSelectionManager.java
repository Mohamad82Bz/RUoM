package me.Mohamad82.RUoM.areaselection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class AreaSelectionManager {

    private final Set<AreaSelection> areaSelections = new HashSet<>();
    private final Set<ItemStack> wands = new HashSet<>();

    private static AreaSelectionManager instance;
    public static AreaSelectionManager getInstance() {
        return instance;
    }

    public AreaSelectionManager() {
        instance = this;
    }

    public void addAreaSelection(AreaSelection areaSelection) {
        areaSelections.add(areaSelection);
    }

    /**
     * @deprecated A player can have multiple area selections, this method returns a random one
     */
    public AreaSelection getPlayerAreaSelection(Player player) {
        for (AreaSelection areaSelection : areaSelections) {
            if (areaSelection.getPlayer().equals(player))
                return areaSelection;
        }
        return null;
    }

    public AreaSelection getPlayerAreaSelection(Player player, ItemStack wand) {
        for (AreaSelection areaSelection : areaSelections) {
            if (areaSelection.getPlayer() != null&& areaSelection.getPlayer().equals(player) &&
                    areaSelection.wand.isSimilar(wand))
                return areaSelection;
        }
        return null;
    }

    public boolean removePlayerAreaSelection(Player player, ItemStack wand) {
        AreaSelection areaSelection = getPlayerAreaSelection(player, wand);

        if (areaSelection != null) {
            areaSelections.remove(areaSelection);
            return true;
        }
        return false;
    }

    public boolean containsPlayerAreaSelection(Player player) {
        for (AreaSelection areaSelection : areaSelections) {
            if (areaSelection.getPlayer() != null && areaSelection.getPlayer().equals(player))
                return true;
        }
        return false;
    }

    public void addWand(ItemStack wand) {
        wands.add(wand);
    }

    public boolean removeWand(ItemStack wand) {
        return wands.remove(wand);
    }

    public Set<ItemStack> getWands() {
        return wands;
    }

}
