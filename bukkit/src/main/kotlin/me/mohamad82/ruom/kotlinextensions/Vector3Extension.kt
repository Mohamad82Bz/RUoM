package me.mohamad82.ruom.kotlinextensions

import me.mohamad82.ruom.math.vector.Vector3
import me.mohamad82.ruom.math.vector.Vector3UtilsBukkit
import org.bukkit.Location
import org.bukkit.World

fun Vector3.toLocation(world: World): Location {
    return Vector3UtilsBukkit.toLocation(world, this)
}