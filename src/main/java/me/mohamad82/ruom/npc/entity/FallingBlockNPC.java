package me.mohamad82.ruom.npc.entity;

import me.mohamad82.ruom.nmsaccessors.BlockAccessor;
import me.mohamad82.ruom.nmsaccessors.FallingBlockEntityAccessor;
import me.mohamad82.ruom.npc.EntityNPC;
import me.mohamad82.ruom.npc.NPCType;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FallingBlockNPC extends EntityNPC {

    private final int data;

    protected FallingBlockNPC(Location location, Material material) throws Exception {
        super(
                FallingBlockEntityAccessor.getConstructor0().newInstance(NMSUtils.getServerLevel(location.getWorld()), location.getX(), location.getY(), location.getZ(), NMSUtils.getBlockState(material)),
                location,
                NPCType.FALLING_BLOCK
        );
        if (!material.isBlock()) {
            throw new IllegalArgumentException("Given material is not a solid block.");
        }
        this.data = (int) BlockAccessor.getMethodGetId1().invoke(null, FallingBlockEntityAccessor.getMethodGetBlockState1().invoke(entity));
    }

    public static FallingBlockNPC fallingBlockNPC(Location location, Material material) {
        try {
            return new FallingBlockNPC(location, material);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void addViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getAddEntityPacket(entity, data),
                PacketUtils.getEntityDataPacket(entity));
    }

}
