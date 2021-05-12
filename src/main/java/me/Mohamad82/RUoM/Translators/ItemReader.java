package me.Mohamad82.RUoM.Translators;

import com.cryptomorin.xseries.XMaterial;
import io.th0rgal.oraxen.items.OraxenItems;
import me.Mohamad82.RUoM.Enums.Hook;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public class ItemReader {

    private final Map<Hook, Boolean> softDepends;
    private final Logger logger;
    private String title = null;
    private SkullBuilder skullBuilder;

    String wrongMaterial = "Could not find '%material%' material! This item will be ignored.";
    String wrongAmount = "Could not read '%amount%' as amount of '%item%' because it is not a valid integer! Amount will be ignored.";
    String wrongItemFlag = "Could not read '%itemflag%' ItemFlag in '%item%' because this ItemFlag does not exist!";
    String wrongEnchantmentLevel = "Could not read '%enchantment%' with '%level%' level in '%item%' because enchantment is unknown or wrongly formatted!";
    String wrongEnchantment = "Could not read '%enchantment%' in '%item%' because enchantment is unknown or wrongly formatted!";
    String wrongSlotName = "Could not read '%slot%' slot(s) because the format is wrong!";
    String wrongSlotNumber = "Could not read '%slot%' slot(s) because it's bigger than gui size!";


    public ItemReader(Map<Hook, Boolean> softDepends, Logger logger, String title) {
        this.softDepends = softDepends;
        this.logger = logger;
        this.title = title;
        this.skullBuilder = new SkullBuilder(logger);
    }

    public ItemReader(Map<Hook, Boolean> softDepends, Logger logger) {
        this.softDepends = softDepends;
        this.logger = logger;
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
                    if (!(softDepends.get(Hook.ORAXEN))) {
                        logger.warning(getTitle() + "You put an Oraxen drop while Oraxen is not installed on the server!");
                        return itemStack;
                    }
                    String[] oraxenItem = string.split(":");
                    if (!(OraxenItems.isAnItem(oraxenItem[1].toLowerCase()))) {
                        logger.warning(getTitle() + '"' + oraxenItem[1] + '"' + "is not a valid Oraxen item!");
                        return itemStack;
                    }
                    itemStack = OraxenItems.getItemById(oraxenItem[1].toLowerCase()).build();
                    noCustomize = true;
                    //MMOItems Compatibility
                } else if (string.startsWith("MMOItem")) {
                    if (!(softDepends.get(Hook.MMOITEMS))) {
                        logger.warning(getTitle() + "You put a MMOItem drop while MMOItems is not installed on the server!");
                        return itemStack;
                    }
                    String[] mmoItem = string.split(":");
                    try {
                        String[] mmoItem2 = mmoItem[1].split(",");
                        itemStack = MMOItems.plugin.getItem(MMOItems.plugin.getTypes().get(mmoItem2[0].toUpperCase()), mmoItem2[1]);
                        noCustomize = true;
                    } catch (Exception e) {
                        logger.warning(getTitle() + "Error while reading MMOItem '" + mmoItem[1] + "'! Please check the format, name and the MMOItem.");
                        return itemStack;
                    }
                } else if (string.startsWith("HEAD") || (string.startsWith("SKULL"))) {
                    isHead = true;
                } else if (itemSplit[0].contains(":")) {
                    String[] itemSplit2 = itemSplit[0].split(":");
                    try {
                        itemStack = XMaterial.valueOf(itemSplit2[0].toUpperCase()).parseItem();
                    } catch (IllegalArgumentException | NullPointerException e) {
                        logger.warning(getTitle() + wrongMaterial
                                .replace("%material%", itemSplit2[0].toUpperCase()));
                        continue;
                    }
                    try {
                        itemStack.setAmount(Integer.parseInt(itemSplit2[1].toUpperCase()));
                    } catch (NumberFormatException e) {
                        logger.warning(getTitle() + wrongAmount
                                .replace("%amount%", itemSplit2[1])
                                .replace("%item%", itemSplit2[0]));
                    }
                } else {
                    try {
                        itemStack = XMaterial.valueOf(itemSplit[0].toUpperCase()).parseItem();
                    } catch (IllegalArgumentException e) {
                        logger.warning(getTitle() + wrongMaterial
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
                                logger.warning(getTitle() + wrongItemFlag
                                        .replace("%itemflag%", strSplit[1].toUpperCase()));
                            }
                        } else if (strSplit[0].equalsIgnoreCase("VALUE") || (strSplit[0].equalsIgnoreCase("BASE64"))) {
                            itemStack = skullBuilder.getHead(strSplit[1]);
                        } else try {
                            itemMeta.addEnchant(Enchantment.getByName(strSplit[0].toUpperCase()), Integer.parseInt(strSplit[1]), true);
                        } catch (IllegalArgumentException e) {
                            logger.warning(getTitle() + wrongEnchantmentLevel
                                    .replace("%enchantment%", strSplit[0].toUpperCase())
                                    .replace("%level%", strSplit[1])
                                    .replace("%item%", itemStack.getType().toString()));
                        }
                    } else if (str.equalsIgnoreCase("UNBREAKABLE")) {
                        itemMeta.setUnbreakable(true);
                    } else try {
                        itemMeta.addEnchant(Enchantment.getByName(str.toUpperCase()), 1, true);
                    } catch (IllegalArgumentException | NullPointerException e) {
                        logger.warning(getTitle() + wrongEnchantment
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
                        logger.warning(getTitle() + wrongSlotName
                                .replace("%slot%", itemSection));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        logger.warning(getTitle() + wrongSlotNumber
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
                    logger.warning(getTitle() + wrongSlotName
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

}
