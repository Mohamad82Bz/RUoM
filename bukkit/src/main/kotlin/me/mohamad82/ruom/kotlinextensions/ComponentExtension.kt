package me.mohamad82.ruom.kotlinextensions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentIteratorType
import net.kyori.adventure.text.TextComponent

fun Component.isBlank(): Boolean {
    this.iterable(ComponentIteratorType.DEPTH_FIRST).forEach { component ->
        if (component !is TextComponent || component.content().isNotBlank()) {
            return false
        }
    }
    return true
}