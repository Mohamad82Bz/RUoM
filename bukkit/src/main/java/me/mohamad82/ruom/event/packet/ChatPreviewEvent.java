package me.mohamad82.ruom.event.packet;

import me.mohamad82.ruom.utils.NMSUtils;
import me.mohamad82.ruom.utils.PacketUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class ChatPreviewEvent {

    public ChatPreviewEvent() {
        PacketListenerManager.getInstance().register(this);
    }

    protected abstract void onPreviewRequest(Player player, int queryId, String message);

    public void sendPreview(Player player, int queryId, Component message) {
        NMSUtils.sendPacket(player, PacketUtils.getChatPreviewPacket(queryId, message));
    }

    public void setDisplayChatPreview(Player player, boolean enabled) {
        NMSUtils.sendPacket(player, PacketUtils.getSetDisplayChatPreviewPacket(enabled));
    }

    public void unregister() {
        PacketListenerManager.getInstance().unregister(this);
    }

}
