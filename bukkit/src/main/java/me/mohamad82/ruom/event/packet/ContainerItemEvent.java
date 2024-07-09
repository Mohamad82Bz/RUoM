package me.mohamad82.ruom.event.packet;

import me.mohamad82.ruom.nmsaccessors.ClientboundContainerSetContentPacketAccessor;
import me.mohamad82.ruom.nmsaccessors.ClientboundContainerSetSlotPacketAccessor;
import me.mohamad82.ruom.utils.NMSUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ContainerItemEvent implements PacketListener {

    public static Set<ContainerItemEvent> HANDLER_LIST = new HashSet<>();

    public ContainerItemEvent() {
        register();
    }

    public abstract ItemStack onItemUpdate(Player player, ItemStack item);

    @Override
    public void register() {
        HANDLER_LIST.add(this);
    }

    @Override
    public void unregister() {
        HANDLER_LIST.remove(this);
    }

    @Override
    public void handle(Player player, Object packet) {
        try {
            if (packet.getClass().equals(ClientboundContainerSetSlotPacketAccessor.getType())) {
                ItemStack item = NMSUtils.getBukkitItemStack(ClientboundContainerSetSlotPacketAccessor.getFieldItemStack().get(packet));
                if (item == null || item.getType().isAir()) return;
                ClientboundContainerSetSlotPacketAccessor.getFieldItemStack().set(packet, NMSUtils.getNmsItemStack(onItemUpdate(player, item.clone())));
            } else if (packet.getClass().equals(ClientboundContainerSetContentPacketAccessor.getType())) {
                List<ItemStack> items = ((List<Object>) ClientboundContainerSetContentPacketAccessor.getFieldItems().get(packet))
                        .stream()
                        .map(NMSUtils::getBukkitItemStack)
                        .collect(Collectors.toList());

                items.replaceAll(item -> item == null || item.getType().isAir() ? item : onItemUpdate(player, item.clone()));

                ClientboundContainerSetContentPacketAccessor.getFieldItems().set(packet, items
                        .stream()
                        .map(NMSUtils::getNmsItemStack)
                        .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
