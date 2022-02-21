package me.mohamad82.ruom.kotlinextensions

import me.mohamad82.ruom.utils.LocUtils
import org.bukkit.Location

fun Location.getAsString(): String {
    return LocUtils.toString(this)
}