package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.*;
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

    /**
     * TODO: I don't know how this works in 1.8
     * @apiNote > 1.9
     */
    public void setHookedEntity(int entityId) {
        if (!ServerVersion.supports(9)) return;
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(),
                ServerVersion.supports(16) ? FishingHookAccessor.getFieldDATA_HOOKED_ENTITY().get(null) : EntityFishingHookAccessor.getFieldB().get(null), entityId + 1));
        sendEntityData();
    }

    /**
     * TODO: I don't know how this works in 1.8
     * @apiNote > 1.9
     */
    public int getHookedEntity() {
        try {
            return (int) SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), FishingHookAccessor.getFieldDATA_HOOKED_ENTITY().get(null)) - 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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
                return EntityFishingHookAccessor.getConstructor0().newInstance(PlayerNPC.createServerPlayerObject("", location.getWorld(), Optional.empty()), NMSUtils.getServerLevel(location.getWorld()), 0, 0);
            } else {
                return EntityFishingHookAccessor.getConstructor1().newInstance(NMSUtils.getServerLevel(location.getWorld()), PlayerNPC.createServerPlayerObject("", location.getWorld(), Optional.empty()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
