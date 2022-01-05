package me.mohamad82.ruom.hologram;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.npc.entity.ThrowableProjectileNPC;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Item2DHologramLine extends ItemHoloLine {

    private Item2DHologramLine(ItemStack item, boolean glowing, float distance) {
        super(item, glowing, distance);
    }

    public static Item2DHologramLine item2DHologramLine(ItemStack item, boolean glow, float distance) {
        return new Item2DHologramLine(item, glow, distance);
    }

    @Override
    public void setItem(ItemStack item) {
        super.setItem(item);
        if (npc != null) {
            getThrowableProjectileNPC().setItem(item);
        }
    }

    @Override
    public void setItem(ItemStack item, Player player) {
        if (npc != null) {
            Ruom.run(() -> NMSUtils.sendPacket(player, PacketUtils.getEntityDataPacket(
                            npc.getId(),
                            (int) EntityDataAccessorAccessor.getMethodGetId1().invoke(ThrowableItemProjectileAccessor.getFieldDATA_ITEM_STACK().get(null)),
                            NMSUtils.getNmsItemStack(item)
            )));
        }
    }

    @Override
    public void setGlowing(boolean glowing) {
        super.setGlowing(glowing);
        if (npc != null) {
            getThrowableProjectileNPC().setGlowing(glowing);
        }
    }

    @Nullable
    protected ThrowableProjectileNPC getThrowableProjectileNPC() {
        return (ThrowableProjectileNPC) npc;
    }

    @Override
    protected void initializeNpc(Location location) {
        npc = ThrowableProjectileNPC.throwableProjectileNPC(location, item);
        npc.setNoGravity(true);
        npc.setGlowing(glowing);
    }

}
