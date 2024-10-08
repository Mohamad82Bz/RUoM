package me.mohamad82.ruom.npc;

import me.mohamad82.ruom.Ruom;
import me.mohamad82.ruom.nmsaccessors.EntityAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class EntityNPC extends NPC {

    private final NPCType npcType;

    protected EntityNPC(Object entity, Location location, NPCType npcType) {
        this.npcType = npcType;

        Ruom.run(() -> {
            EntityAccessor.getMethodSetPos1().invoke(entity, location.getX(), location.getY(), location.getZ());
            EntityAccessor.getMethodSetRot1().invoke(entity, location.getYaw(), location.getPitch());
            initialize(entity);
        });
    }

    public NPCType getType() {
        return npcType;
    }

    @Override
    protected void addViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getAddEntityPacket(entity),
                PacketUtils.getEntityDataPacket(entity));
    }

    @Override
    protected void removeViewer(Player player) {
        NMSUtils.sendPacket(player,
                PacketUtils.getRemoveEntitiesPacket(id));
    }

}
