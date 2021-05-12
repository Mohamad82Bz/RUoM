package me.Mohamad82.RUoM.GUI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GUI extends GUIAnimator implements InventoryHolder {

    protected Inventory inventory;

    public GUI(String title) {
        super(null);
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    public GUI(JavaPlugin plugin, String title) {
        super(plugin);
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    public GUI(InventoryType type, String title) {
        super(null);
        this.inventory = Bukkit.createInventory(this, type, title);
    }

    public GUI(JavaPlugin plugin, InventoryType type, String title) {
        super(plugin);
        this.inventory = Bukkit.createInventory(this, type, title);
    }

    public GUI(int size, String title) {
        super(null);
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public GUI(JavaPlugin plugin, int size, String title) {
        super(plugin);
        this.inventory = Bukkit.createInventory(this, size, title);
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
     * This method is called as the player closes the inventory. <br>
     * It can be used to save results from their interactions, for example.
     * @param player The player that closed the inventory
     */
    public abstract void onClose(Player player);

    /**
     *  Helper function designed to fill in empty spaces of the menu. <br>
     *  Can be overriden to define custom behavior
     * @param item What wil fill the empty spaces
     */
    protected void fill(ItemStack item) {
        for(int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null ) continue;
            inventory.setItem(i, item);
        }
    }

    /**
     * Gets the menu's inventory. This is called typically called like so: <br><b>Player#openInventory(Menu#getInventory())</b>
     * @return inventory - the menu's inventory
     */
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}