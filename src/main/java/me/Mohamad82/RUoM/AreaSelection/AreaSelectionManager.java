package me.Mohamad82.RUoM.AreaSelection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class AreaSelectionManager {

    Set<AreaSelection> areaSelections = new HashSet<>();
    final protected ItemStack wand;

    private static AreaSelectionManager instance;
    public static AreaSelectionManager getInstance() {
        return instance;
    }

    public AreaSelectionManager(ItemStack wand) {
        instance = this;
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

}
