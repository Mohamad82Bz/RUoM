package me.mohamad82.ruom.kotlinextensions

import me.mohamad82.ruom.utils.NMSUtils
import net.kyori.adventure.text.Component
import org.bukkit.block.Sign

fun Sign.setLine(line: Int, component: Component) {
    NMSUtils.setSignLine(this, line, component)
}

fun Sign.getLineInComponent(line: Int): Component {
    return NMSUtils.getSignLine(this, line)
}

fun Sign.getLineInComponents(): List<Component> {
    return NMSUtils.getSignLines(this)
}

fun Sign.updateSign() {
    NMSUtils.updateSign(this)
}