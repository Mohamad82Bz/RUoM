package me.mohamad82.ruom.kotlinextensions

import io.netty.channel.Channel
import me.mohamad82.ruom.adventure.AdventureApi
import me.mohamad82.ruom.math.vector.Vector3
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit
import me.mohamad82.ruom.toast.ToastMessage
import me.mohamad82.ruom.utils.NMSUtils
import me.mohamad82.ruom.utils.PlayerUtils
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.sendMessage(message: Component) {
    AdventureApi.get().player(this).sendMessage(message)
}

fun Player.sendActionBar(message: Component) {
    NMSUtils.sendActionBar(this, message)
}

fun Player.sendToast(message: ToastMessage) {
    message.send(this)
}

fun Player.getChannel(): Channel {
    return NMSUtils.getChannel(this)
}

fun Player.getVector3Location(): Vector3 {
    return Vector3UtilsBukkit.toVector3(this.location)
}

fun Player.disconnect(message: Component) {
    NMSUtils.disconnect(this, message)
}

fun Player.sendPacket(packet: Any) {
    NMSUtils.sendPacket(this, packet)
}

fun Player.getTotalItemAmount(item: ItemStack): Int {
    return PlayerUtils.getTotalItemAmount(this, item)
}

fun Player.hasEmptySpaceFor(item: ItemStack): Boolean {
    return PlayerUtils.hasEmptySpaceFor(this, item)
}

fun Player.removeItem(item: ItemStack, amount: Int) {
    PlayerUtils.removeItem(this, item, amount)
}

fun Player.hasItemInMainHand(material: Material): Boolean {
    return PlayerUtils.hasItemInMainHand(this, material)
}

fun Player.hasItemInOffHand(material: Material): Boolean {
    return PlayerUtils.hasItemInOffHand(this, material)
}

fun Player.hasItemInHand(material: Material): Boolean {
    return PlayerUtils.hasItemInHand(this, material)
}

fun Player.getRightHandLocation(): Location {
    return PlayerUtils.getRightHandLocation(this.location)
}