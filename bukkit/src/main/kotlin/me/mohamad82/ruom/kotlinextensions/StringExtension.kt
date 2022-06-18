package me.mohamad82.ruom.kotlinextensions

import me.mohamad82.ruom.adventure.AdventureApi
import me.mohamad82.ruom.adventure.ComponentUtils
import me.mohamad82.ruom.string.StringUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

fun String.capitalize(): String {
    return StringUtils.capitalize(this)
}

fun String.component(): Component {
    return MiniMessage.miniMessage().deserialize(this)
}

fun String.component(vararg tags: TagResolver): Component {
    return MiniMessage.miniMessage().deserialize(this, *tags)
}