package me.mohamad82.ruom.event.packet;

import me.mohamad82.ruom.npc.LivingEntityNPC;
import me.mohamad82.ruom.vector.Vector3;
import org.bukkit.entity.Player;

/**
 * A packet-based event that triggers whenever a player interact on both server-sided and client-sided (sent by packets) entities.
 * Usable for NPCs.
 */
public abstract class PlayerInteractAtEntityEvent {

    public PlayerInteractAtEntityEvent() {
        PacketListenerManager.getInstance().register(this);
    }

    protected abstract void onInteract(Player player, LivingEntityNPC.InteractionHand hand, int entityId);

    protected abstract void onInteractAt(Player player, LivingEntityNPC.InteractionHand hand, Vector3 location, int entityId);

    protected abstract void onAttack(Player player, int entityId);

    public void unregister() {
        PacketListenerManager.getInstance().unregister(this);
    }

}
