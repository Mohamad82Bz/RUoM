package me.mohamad82.ruom.kotlinextensions

import me.mohamad82.ruom.utils.NMSUtils
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

fun ItemStack.toComponent(): Component {
    return NMSUtils.getItemStackComponent(this)
}

fun ItemStack.toJson(): String {
    return NMSUtils.getItemStackNBTJson(this)
}

fun ItemStack.withDisplayName(displayName: Component): ItemStack {
    return NMSUtils.setDisplayName(this, displayName)
}

fun ItemStack.withLore(lines: List<Component>): ItemStack {
    return NMSUtils.setLore(this, lines)
}