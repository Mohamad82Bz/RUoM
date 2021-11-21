package me.Mohamad82.RUoM.translators;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XMaterial;
import io.th0rgal.oraxen.items.OraxenItems;
import me.Mohamad82.RUoM.utils.ServerVersion;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public class ItemReader {
    
    private final static Class<?> CRAFT_ITEMSTACK, ITEMSTACK, ITEM, CREATIVE_MODE_TAB;

    static {
        CRAFT_ITEMSTACK = ReflectionUtils.getCraftClass("inventory.CraftItemStack");
        ITEMSTACK = ReflectionUtils.getNMSClass("world.item", "ItemStack");
        ITEM = ReflectionUtils.getNMSClass("world.item", "Item");
        CREATIVE_MODE_TAB = ReflectionUtils.getNMSClass("world.item", "CreativeModeTab");
    }

    private final Set<Hook> softDepends;
    private final Logger logger;
    private String title = null;
    private final SkullBuilder skullBuilder;

    String wrongMaterial = "Could not find '%material%' material! This item will be ignored.";
    String wrongAmount = "Could not read '%amount%' as amount of '%item%' because it is not a valid integer! Amount will be ignored.";
    String wrongItemFlag = "Could not read '%itemflag%' ItemFlag in '%item%' because this ItemFlag does not exist!";
    String wrongEnchantmentLevel = "Could not read '%enchantment%' with '%level%' level in '%item%' because enchantment is unknown or wrongly formatted!";
    String wrongEnchantment = "Could not read '%enchantment%' in '%item%' because enchantment is unknown or wrongly formatted!";
    String wrongSlotName = "Could not read '%slot%' slot(s) because the format is wrong!";
    String wrongSlotNumber = "Could not read '%slot%' slot(s) because it's bigger than gui size!";

    public ItemReader(Logger logger, String title) {
        this.softDepends = getInstalledDependencies();
        this.logger = logger;
        this.title = title;
        this.skullBuilder = new SkullBuilder();
    }

    public ItemReader(Logger logger) {
        this.softDepends = getInstalledDependencies();
        this.logger = logger;
        this.skullBuilder = new SkullBuilder();
    }

    public ItemReader() {
        this.softDepends = getInstalledDependencies();
        this.logger = null;
        this.skullBuilder = new SkullBuilder();
    }
    
    public static Category getItemCategory(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        try {
            Object nmsItemStack = CRAFT_ITEMSTACK.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            Object nmsItem = ITEMSTACK.getMethod("getItem").invoke(nmsItemStack);

            Object creativeModeTab;
            if (ServerVersion.supports(9)) {
                creativeModeTab = ITEM.getMethod(ServerVersion.supports(17) ? "t" : ServerVersion.supports(16) ? "q" : "b").invoke(nmsItem);
            } else {
                Field creativeModeTabField = ITEM.getDeclaredField("b");
                creativeModeTabField.setAccessible(true);
                creativeModeTab = creativeModeTabField.get(nmsItem);
            }
            Field field = CREATIVE_MODE_TAB.getDeclaredField(ServerVersion.supports(9) ? "p" : "o");
            field.setAccessible(true);

            String creativeModeTabName = (String) field.get(creativeModeTab);

            Category category;
            if (creativeModeTabName.equals("buildingBlocks")) {
                category = Category.BLOCKS;
            } else {
                category = Category.valueOf(creativeModeTabName);
            }
            return category;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ItemStack toItemStack(String string) {
        ItemStack itemStack = new ItemStack(Material.STONE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        String[] itemSplit = string.split(" ; ");
        boolean isFirstOne = true;
        boolean noCustomize = false;
        boolean isHead = false;
        for (String str : itemSplit) {
            if (isFirstOne) {
                //Oraxen Compatibility
                if (string.startsWith("Oraxen")) {
                    if (!(softDepends.contains(Hook.ORAXEN))) {
                        warn(getTitle() + "You put an Oraxen item while Oraxen is not installed on the server!");
                        return itemStack;
                    }
                    String[] oraxenItem = string.split(":");
                    if (!(OraxenItems.isAnItem(oraxenItem[1].toLowerCase()))) {
                        warn(getTitle() + '"' + oraxenItem[1] + '"' + "is not a valid Oraxen item!");
                        return itemStack;
                    }
                    itemStack = OraxenItems.getItemById(oraxenItem[1].toLowerCase()).build();
                    noCustomize = true;
                    //MMOItems Compatibility
                } else if (string.startsWith("MMOItem")) {
                    if (!(softDepends.contains(Hook.MMOITEMS))) {
                        warn(getTitle() + "You put a MMO Item while MMOItems is not installed on the server!");
                        return itemStack;
                    }
                    String[] mmoItem = string.split(":");
                    try {
                        String[] mmoItem2 = mmoItem[1].split(",");
                        itemStack = MMOItems.plugin.getItem(MMOItems.plugin.getTypes().get(mmoItem2[0].toUpperCase()), mmoItem2[1]);
                        noCustomize = true;
                    } catch (Exception e) {
                        warn(getTitle() + "Error while reading MMOItem '" + mmoItem[1] + "'! Please check the format, name and the MMOItem.");
                        return itemStack;
                    }
                } else if (string.startsWith("HEAD") || (string.startsWith("SKULL"))) {
                    isHead = true;
                } else if (itemSplit[0].contains(":")) {
                    String[] itemSplit2 = itemSplit[0].split(":");
                    try {
                        itemStack = XMaterial.valueOf(itemSplit2[0].toUpperCase()).parseItem();
                    } catch (IllegalArgumentException | NullPointerException e) {
                        warn(getTitle() + wrongMaterial
                                .replace("%material%", itemSplit2[0].toUpperCase()));
                        continue;
                    }
                    try {
                        itemStack.setAmount(Integer.parseInt(itemSplit2[1].toUpperCase()));
                    } catch (NumberFormatException e) {
                        warn(getTitle() + wrongAmount
                                .replace("%amount%", itemSplit2[1])
                                .replace("%item%", itemSplit2[0]));
                    }
                } else {
                    try {
                        itemStack = XMaterial.valueOf(itemSplit[0].toUpperCase()).parseItem();
                    } catch (IllegalArgumentException e) {
                        warn(getTitle() + wrongMaterial
                                .replace("%material%", itemSplit[0].toUpperCase()));
                        continue;
                    }
                }
            }
            //Customization
            if (!isFirstOne) {
                if (!noCustomize) {
                    if (str.contains(":")) {
                        String[] strSplit = str.split(":", 2);
                        if (strSplit[0].equalsIgnoreCase("NAME")) {
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', strSplit[1]));
                        } else if (strSplit[0].equalsIgnoreCase("LORE")) {
                            List<String> lore = new ArrayList<>();
                            String[] lores = strSplit[1].split("\\|");
                            for (String loreString : lores) {
                                lore.add(ChatColor.translateAlternateColorCodes('&', loreString));
                            }
                            itemMeta.setLore(lore);
                        } else if (strSplit[0].equalsIgnoreCase("ITEMFLAG")) {
                            try {
                                itemMeta.addItemFlags(ItemFlag.valueOf(strSplit[1].toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                warn(getTitle() + wrongItemFlag
                                        .replace("%itemflag%", strSplit[1].toUpperCase()));
                            }
                        } else if (strSplit[0].equalsIgnoreCase("VALUE") || (strSplit[0].equalsIgnoreCase("BASE64"))) {
                            itemStack = skullBuilder.getHead(strSplit[1]);
                        } else try {
                            itemMeta.addEnchant(Enchantment.getByName(strSplit[0].toUpperCase()), Integer.parseInt(strSplit[1]), true);
                        } catch (IllegalArgumentException e) {
                            warn(getTitle() + wrongEnchantmentLevel
                                    .replace("%enchantment%", strSplit[0].toUpperCase())
                                    .replace("%level%", strSplit[1])
                                    .replace("%item%", itemStack.getType().toString()));
                        }
                    } else if (str.equalsIgnoreCase("UNBREAKABLE")) {
                        itemMeta.setUnbreakable(true);
                    } else try {
                        itemMeta.addEnchant(Enchantment.getByName(str.toUpperCase()), 1, true);
                    } catch (IllegalArgumentException | NullPointerException e) {
                        warn(getTitle() + wrongEnchantment
                                .replace("%enchantment%", str.toUpperCase())
                                .replace("%item%", itemStack.getType().toString()));
                    }
                }
            }
            if (itemMeta != null) {
                if (isHead) {
                    ItemMeta headMeta = itemStack.getItemMeta();
                    headMeta.setDisplayName(itemMeta.getDisplayName());
                    headMeta.setLore(itemMeta.getLore());
                    headMeta.addItemFlags(itemMeta.getItemFlags().toArray(new ItemFlag[0]));
                    for (Enchantment enchant : itemMeta.getEnchants().keySet())
                        headMeta.addEnchant(enchant, itemMeta.getEnchantLevel(enchant), true);

                    itemStack.setItemMeta(headMeta);
                } else
                    itemStack.setItemMeta(itemMeta);
            }

            isFirstOne = false;
        }
        return itemStack;
    }

    public ItemStack[] getContent(List<?> objectList, Boolean isPlayerInventory, int size) {
        if (isPlayerInventory)
            size = 41;
        ItemStack[] content = new ItemStack[size];
        for (Object objectItem : objectList) {
            String item = objectItem.toString().substring(1, objectItem.toString().length() - 1);
            ItemStack itemStack;

            String[] strings = item.split("=", 2);
            String itemSection = strings[0];

            itemStack = toItemStack(strings[1]);

            try {
                if (itemSection.contains("-")) {
                    String[] itemSectionSplit = itemSection.split("-");
                    try {
                        for (int i=0 ; i <= Integer.parseInt(itemSectionSplit[1]) - Integer.parseInt(itemSectionSplit[0]) ; i++) {
                            int n = Integer.parseInt(itemSectionSplit[0]) + i;
                            content[n] = itemStack;
                        }
                    } catch (IllegalArgumentException e) {
                        warn(getTitle() + wrongSlotName
                                .replace("%slot%", itemSection));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        warn(getTitle() + wrongSlotNumber
                                .replace("%slot%", itemSection));
                    }
                } else try {
                    if (!(isPlayerInventory && itemSection.equalsIgnoreCase("OFFHAND") ||
                            itemSection.equalsIgnoreCase("HELMET") ||
                            itemSection.equalsIgnoreCase("CHESTPLATE") ||
                            itemSection.equalsIgnoreCase("LEGGINGS") ||
                            itemSection.equalsIgnoreCase("BOOTS")))
                        content[Integer.parseInt(itemSection)] = itemStack;
                } catch (IllegalArgumentException e) {
                    warn(getTitle() + wrongSlotName
                            .replace("%slot%", itemSection));
                }
            } catch (NumberFormatException ignore) {
            }
            if (isPlayerInventory) {
                if (itemSection.equalsIgnoreCase("OFFHAND")) {
                    content[40] = itemStack;
                }
                if (itemSection.equalsIgnoreCase("HELMET")) {
                    content[39] = itemStack;
                }
                if (itemSection.equalsIgnoreCase("CHESTPLATE")) {
                    content[38] = itemStack;
                }
                if (itemSection.equalsIgnoreCase("LEGGINGS")) {
                    content[37] = itemStack;
                }
                if (itemSection.equalsIgnoreCase("BOOTS")) {
                    content[36] = itemStack;
                }
            }
        }
        return content;
    }

    public ItemStack[] getContent(List<?> objectList, boolean isPlayerInventory) {
        return getContent(objectList, isPlayerInventory, 54);
    }

    public ItemStack[] getContent(List<?> objectList) {
        return getContent(objectList, false, 54);
    }

    private String getTitle() {
        return title == null ? "" : "[" + title + "] ";
    }

    private void log(String message) {
        if (logger != null)
            logger.info(message);
    }

    private void warn(String message) {
        if (logger != null)
            logger.warning(message);
    }

    private Set<Hook> getInstalledDependencies() {
        Set<Hook> softDepends = new HashSet<>();

        try {
            Class.forName("io.th0rgal.oraxen.items.OraxenItems");
            softDepends.add(Hook.ORAXEN);
        } catch (ClassNotFoundException ignored) {}

        try {
            Class.forName("net.Indyuce.mmoitems.MMOItems");
            softDepends.add(Hook.MMOITEMS);
        } catch (ClassNotFoundException ignored) {}

        return softDepends;
    }

    private enum Hook {
        ORAXEN,
        MMOITEMS
    }

    public enum Category {
        BLOCKS,
        DECORATIONS,
        REDSTONE,
        TRANSPORTATION,
        MISC,
        FOOD,
        TOOLS,
        COMBAT,
        BREWING
    }

}
