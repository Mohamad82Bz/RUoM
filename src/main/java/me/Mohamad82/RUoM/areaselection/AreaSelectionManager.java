package me.Mohamad82.RUoM.areaselection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class AreaSelectionManager {

    Set<AreaSelection> areaSelections;
    final protected ItemStack wand;

    private static AreaSelectionManager instance;
    public static AreaSelectionManager getInstance() {
        return instance;
    }

    public AreaSelectionManager(ItemStack wand) {
        instance = this;
        areaSelections = new HashSet<>();
        this.wand = wand;
    }

    public void addAreaSelection(AreaSelection areaSelection) {
        areaSelections.add(areaSelection);
    }

    public AreaSelection getPlayerAreaSection(Player player) {
        for (AreaSelection areaSelection : areaSelections) {
            if (areaSelection.getPlayer().equals(player))
                return areaSelection;
        }
        return null;
    }

    public boolean containsPlayerAreaSelection(Player player) {
        for (AreaSelection areaSelection : areaSelections) {
            if (areaSelection.getPlayer().equals(player))
                return true;
        }
        return false;
    }

}
