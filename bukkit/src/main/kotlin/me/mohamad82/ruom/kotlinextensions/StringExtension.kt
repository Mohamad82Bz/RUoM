package me.mohamad82.ruom.kotlinextensions

import me.mohamad82.ruom.string.StringUtils

fun String.capitalize(): String {
    return StringUtils.capitalize(this)
}