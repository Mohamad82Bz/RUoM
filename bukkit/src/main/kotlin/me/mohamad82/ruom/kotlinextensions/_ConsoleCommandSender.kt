package me.mohamad82.ruom.kotlinextensions

import me.mohamad82.ruom.adventure.AdventureApi
import net.kyori.adventure.text.Component
import org.bukkit.command.ConsoleCommandSender

fun ConsoleCommandSender.sendMessage(message: Component) {
    AdventureApi.get().console().sendMessage(message)
}