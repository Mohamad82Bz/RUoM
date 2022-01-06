package me.mohamad82.ruom.npc.entity;

import com.cryptomorin.xseries.XMaterial;
import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.nmsaccessors.ThrownTridentAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class TridentNPC extends EntityNPC {
    
    protected TridentNPC(Location location, ItemStack tridentItem) throws Exception {
        super(
                ThrownTridentAccessor.getConstructor0().newInstance(NMSUtils.getServerLevel(location.getWorld()), null, NMSUtils.getNmsItemStack(tridentItem)),
                location,
                NPCType.TRIDENT
        );
        Ruom.run(() -> EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ()));
    }

    public static TridentNPC tridentNPC(Location location, ItemStack tridentItem) {
        if (tridentItem.getType() != XMaterial.TRIDENT.parseMaterial()) {
            throw new IllegalArgumentException("ItemStack must be a trident.");
        }
        try {
            return new TridentNPC(location, tridentItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setFoil(boolean foil) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), ThrownTridentAccessor.getFieldID_FOIL().get(null), foil));
        sendEntityData();
    }

    public boolean isFoil() {
        try {
            return (boolean) SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), ThrownTridentAccessor.getFieldID_FOIL().get(null));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
