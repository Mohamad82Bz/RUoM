package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.nmsaccessors.ThrownTridentAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.Location;

/**
 * @apiNote > 1.13
 */
public class TridentNPC extends EntityNPC {
    
    protected TridentNPC(Location location, byte loyalty, boolean enchanted) throws Exception {
        super(
                ThrownTridentAccessor.getConstructor0().newInstance(NPCType.TRIDENT.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.TRIDENT
        );
        SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), ThrownTridentAccessor.getFieldID_LOYALTY().get(null), loyalty);
        if (ServerVersion.supports(15)) {
            setFoil(enchanted);
        }
    }

    public static TridentNPC tridentNPC(Location location, byte loyalty, boolean enchanted) {
        try {
            return new TridentNPC(location, loyalty, enchanted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static TridentNPC tridentNPC(Location location, byte loyalty) {
        return tridentNPC(location, loyalty, false);
    }

    public static TridentNPC tridentNPC(Location location) {
        return tridentNPC(location, (byte) 0);
    }

    /**
     * @apiNote > 1.15
     */
    public void setFoil(boolean foil) {
        if (!ServerVersion.supports(15)) return;
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), ThrownTridentAccessor.getFieldID_FOIL().get(null), foil));
        sendEntityData();
    }

    /**
     * @apiNote > 1.15
     */
    public boolean isFoil() {
        if (!ServerVersion.supports(15)) return false;
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
