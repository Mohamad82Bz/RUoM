package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.*;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.Location;

import java.lang.reflect.Field;

public class VillagerNPC extends EntityNPC {

    protected VillagerNPC(Location location, VillagerType type, VillagerProfession profession) throws Exception {
        super(
                VillagerAccessor.getConstructor0().newInstance(NPCType.VILLAGER.getNmsEntityType(), NMSUtils.getServerLevel(location.getWorld())),
                location,
                NPCType.VILLAGER
        );
        EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ());
        setVillagerData(type, profession);
    }

    public static VillagerNPC villagerNPC(Location location, VillagerType type, VillagerProfession profession) {
        try {
            return new VillagerNPC(location, type, profession);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setVillagerData(VillagerType type, VillagerProfession profession) {
        Ruom.run(() -> SynchedEntityDataAccessor.getMethodSet1().invoke(getEntityData(),
                VillagerAccessor.getFieldDATA_VILLAGER_DATA().get(null), VillagerDataAccessor.getConstructor0().newInstance(type.nmsObject, profession.nmsObject, 1)));
        sendEntityData();
    }

    public VillagerType getVillagerType() {
        try {
            return VillagerType.valueOf(VillagerDataAccessor.getMethodGetType1().invoke(SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), VillagerAccessor.getFieldDATA_VILLAGER_DATA().get(null))).toString().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public VillagerProfession getVillagerProfession() {
        try {
            return VillagerProfession.valueOf(VillagerDataAccessor.getMethodGetProfession1().invoke(SynchedEntityDataAccessor.getMethodGet1().invoke(getEntityData(), VillagerAccessor.getFieldDATA_VILLAGER_DATA().get(null))).toString().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static enum VillagerProfession {
        NONE,
        ARMORER,
        BUTCHER,
        CARTOGRAPHER,
        CLERIC,
        FARMER,
        FISHERMAN,
        FLETCHER,
        LEATHERWORKER,
        LIBRARIAN,
        MASON,
        NITWIT,
        SHEPHERD,
        TOOLSMITH,
        WEAPONSMITH;
        
        private Object nmsObject;
        
        VillagerProfession() {
            Ruom.run(() -> nmsObject = ((Field) VillagerProfessionAccessor.class.getMethod("getField" + this).invoke(null)).get(null));
        }
    }

    public static enum VillagerType {
        DESERT,
        JUNGLE,
        PLAINS,
        SAVANNA,
        SNOW,
        SWAMP,
        TAIGA;
        
        private Object nmsObject;
        
        VillagerType() {
            Ruom.run(() -> nmsObject = ((Field) VillagerTypeAccessor.class.getMethod("getField" + this).invoke(null)).get(null));
        }
    }

}
