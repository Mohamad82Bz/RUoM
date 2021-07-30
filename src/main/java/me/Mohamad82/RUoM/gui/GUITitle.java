package me.Mohamad82.RUoM.gui;

import com.cryptomorin.xseries.ReflectionUtils;
import me.Mohamad82.RUoM.utils.ServerVersion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GUITitle {

    private static Method getHandle, sendPacket, updateInventory, getName, getTitle, getMCTitle, getInventory;
    private static Field ac, wi, pc;
    private static Class<?> ppoow, icbc, cs, cic, ci, mci;
    private static Constructor<?> chatMessageCon;

    static {
        try {
            getHandle = ReflectionUtils.getCraftClass("entity.CraftPlayer").getMethod("getHandle");
            sendPacket = ReflectionUtils.getNMSClass("server.network", "PlayerConnection").getMethod("sendPacket");

            Class<?> entityPlayer = ReflectionUtils.getNMSClass("server.level", "EntityPlayer");
            Class<?> container = ReflectionUtils.getNMSClass("world.inventory", "Container");
            cs = ReflectionUtils.getNMSClass("world.inventory", "Containers");

            pc = getField(entityPlayer, "playerConnection");
            ac = getField(entityPlayer, "activeContainer");
            wi = getField(container, "windowId");

            updateInventory = entityPlayer.getMethod("updateInventory", container);

            cic = ReflectionUtils.getCraftClass("inventory.CraftInventoryCustom");
            ci = ReflectionUtils.getCraftClass("inventory.CraftInventory");
            mci = cic.getDeclaredClasses()[0];
            getInventory = getMethod(ci, "getInventory");
            Class<?> inventory = Inventory.class;
            getTitle = getMethod(inventory, "getTitle");
            getName = getMethod(inventory, "getName");
            getMCTitle = getMethod(mci, "getTitle");
            getMCTitle.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Constructor<?> getConstructor(Class<?> c, Class<?>... args) {
        if (c == null) return null;
        try {
            return c.getConstructor(args);
        } catch (Exception e) {
            try {
                return c.getDeclaredConstructor(args);
            } catch (NoSuchMethodException noSuchMethodException) {
                return null;
            }
        }
    }

    private static Field getField(Class<?> c, String name) {
        if (c == null) return null;
        try {
            return c.getField(name);
        } catch (Exception e) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException noSuchFieldException) {
                return null;
            }
        }
    }

    private static Method getMethod(Class<?> c, String name, Class<?>... args) {
        if (c == null) return null;
        try {
            return c.getMethod(name, args);
        } catch (Exception e) {
            try {
                return c.getDeclaredMethod(name, args);
            } catch (NoSuchMethodException noSuchMethodException) {
                return null;
            }
        }
    }

    public static void update(Player player, String newTitle) {
        try {
            if (player.getOpenInventory().getTopInventory() == null) return;
            Inventory inv = player.getOpenInventory().getTopInventory();
            Object ep = getHandle.invoke(player);
            Object activeContainer = ac.get(ep);
            Object chatMessage = chatMessageCon.newInstance(newTitle);
            int id = (int) wi.get(activeContainer);
            if (ServerVersion.supports(13)) {
                Constructor<?> con = ppoow.getConstructor(int.class, cs, icbc);
                sendPacket.invoke(pc.get(ep),
                        con.newInstance(id, cs.getField("GENERIC_9X" + (inv.getSize() / 9)).get(null), chatMessage));
            } else {
                Constructor<?> con = ppoow.getConstructor(int.class, String.class, icbc, int.class);
                sendPacket.invoke(pc.get(ep),
                        con.newInstance(id, "minecraft:chest", chatMessage, inv.getSize()));
            }
            updateInventory.invoke(ep, activeContainer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getName(Inventory inv) {
        try {
            return (String) getName.invoke(inv);
        } catch (Exception ignored) {
            try {
                return (String) getTitle.invoke(inv);
            } catch (Exception ignored1) {
                try {
                    String title = "";
                    if (inv.getClass().isAssignableFrom(cic))
                        title = (String) getMCTitle.invoke(mci.cast(getInventory.invoke(ci.cast(inv))));
                    return inv.getClass().isAssignableFrom(cic) ? title : inv.getType().getDefaultTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error";
                }
            }
        }
    }
}
