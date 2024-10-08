package me.mohamad82.ruom.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class GUI extends GUIAnimator implements InventoryHolder {

    protected Inventory inventory;

    public GUI(String title) {
        this.inventory = Bukkit.createInventory(this, 54, title);
        setGUI(inventory);
    }

    public GUI(InventoryType type, String title) {
        this.inventory = Bukkit.createInventory(this, type, title);
        setGUI(inventory);
    }

    public GUI(int size, String title) {
        this.inventory = Bukkit.createInventory(this, size, title);
        setGUI(inventory);
    }

    /**
     * This method is called when a player perform a click in the inventory.
     *
     * @param player The player that clicked the inventory (event.getWhoClicked())
     * @param slot The raw slot that the player clicked (event.getRawSlot()).
     *             Remember that this can excede the bounds of your custom inventory.
     * @param type The ClickType they used to click the inventory (event.getClick())
     * @return <b>true</b>, if the event should be cancelled, <b>false</b> otherwise.
     */
    public abstract boolean onClick(Player player, int slot, ClickType type);

    /**
     * This method is called right as the player is opening the inventory.
     * It can be used to build the inventory dynamically, for example.
     * @param player The player that opened the inventory
     */
    public abstract void onOpen(Player player);

    /**
     * This method is called as the player closes the inventory.
     * It can be used to save results from their interactions, for example.
     * @param player The player that closed the inventory
     */
    public abstract void onClose(Player player);

    /**
     *  Helper function designed to fill in empty spaces of the menu.
     *  Can be overriden to define custom behavior
     * @param item What wil fill the empty spaces
     */
    protected void fill(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null) continue;
            inventory.setItem(i, item);
        }
    }

    /**
     * Gets the menu's inventory
     * @return inventory - the menu's inventory
     */
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets a clonned version of the menu's inventory. Click Events will execute on this clonned version
     * @return inventory - the clonned version of menu's inventory
     */
    public Inventory getClonnedInventory() {
        Inventory clonnedInventory = Bukkit.createInventory(inventory.getHolder(),
                inventory.getSize(), inventory.getType().getDefaultTitle());
        clonnedInventory.setContents(inventory.getContents());

        return clonnedInventory;
    }

}