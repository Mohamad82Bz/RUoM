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