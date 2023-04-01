package me.mohamad82.ruom.kotlinextensions

import com.google.gson.JsonElement
import java.util.*

fun JsonElement.asUuid(): UUID {
    return UUID.fromString(this.asString)
}