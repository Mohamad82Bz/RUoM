package me.mohamad82.ruom.kotlinextensions

import me.mohamad82.ruom.math.vector.Vector3
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit
import me.mohamad82.ruom.utils.LocUtils
import org.bukkit.Location

fun Location.getAsString(): String {
    return LocUtils.toString(this)
}

fun Location.toVector3(): Vector3 {
    return Vector3UtilsBukkit.toVector3(this)
}