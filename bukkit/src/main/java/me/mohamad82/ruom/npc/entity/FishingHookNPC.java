package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.nmsaccessors.FishingHookAccessor;
import me.mohamad82.ruom.nmsaccessors.SynchedEntityDataAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.npc.PlayerNPC;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import me.mohamad82.ruom.utils.ServerVersion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class FishingHookNPC extends EntityNPC {

    private final int ownerEntityId;

    protected FishingHookNPC(Location location, int ownerEntityId) throws Exception {
        super(
                createFishingHookObject(location),
                location,
                NPCType.FISHING_BOBBER
        );
        EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ());
        this.ownerEntityId = ownerEntityId;
    }

    public static FishingHookNPC fishingHookNPC(Location location, int ownerEntityId) {
        try {
            return new FishingHookNPC(location, ownerEntityId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setHookedEntity(int entityId) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(), FishingHookAccessor.getFieldDATA_HOOKED_ENTITY().get(null), entityId));
        sendEntityData();
    }

    @Override
    protected void addViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getAddEntityPacket(entity, ownerEntityId),
                PacketUtils.getEntityDataPacket(entity));
    }

    public static Object createFishingHookObject(Location location) {
        try {
            if (ServerVersion.supports(17)) {
                return FishingHookAccessor.getConstructor0().newInstance(NPCType.FISHING_BOBBER.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld()));
            } else if (ServerVersion.supports(14)) {
                return FishingHookAccessor.getConstructor1().newInstance(PlayerNPC.createServerPlayerObject("", location.getWorld(), Optional.empty()), NMSUtils.getServerLevel(location.getWorld()), 0, 0);
            } else {
                return FishingHookAccessor.getConstructor2().newInstance(NMSUtils.getServerLevel(location.getWorld()), PlayerNPC.createServerPlayerObject("", location.getWorld(), Optional.empty()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
