package net.craftoriya.adaptersLib.adapter

import net.craftoriya.adaptersLib.port.IPlayerFeedbackPort
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.UUID

class BukkitPlayerFeedbackAdapter : IPlayerFeedbackPort {
    override fun actionBar(id: UUID, text: String) {
        Bukkit.getPlayer(id)?.sendActionBar(Component.text(text))
    }
}