package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.nmsaccessors.ThrownTridentAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.Location;

public class TridentNPC extends EntityNPC {
    
    protected TridentNPC(Location location, byte loyalty, boolean enchanted) throws Exception {
        super(
                ThrownTridentAccessor.getConstructor0().newInstance(NPCType.TRIDENT.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.TRIDENT
        );
        SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), ThrownTridentAccessor.getFieldID_LOYALTY().get(null), loyalty);
        setFoil(enchanted);
        EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ());
    }

    public static TridentNPC tridentNPC(Location location, byte loyalty, boolean enchanted) {
        try {
            return new TridentNPC(location, loyalty, enchanted);
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

    public void collect(int collectorEntityId) {
        super.collect(id, collectorEntityId, 1);
    }
    
}
