package net.craftoriya.adaptersLib.adapter

import net.craftoriya.adaptersLib.port.IPlayerFeedbackPort
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.UUID

class BukkitPlayerFeedbackAdapter : IPlayerFeedbackPort {
    override fun actionBar(id: UUID, text: String) {
        Bukkit.getPlayer(id)?.sendActionBar(Component.text(text))
    }

    override fun glowHeldItem(id: UUID, glow: Boolean) {
        val inv = Bukkit.getPlayer(id)?.inventory ?: return
        val item = inv.itemInMainHand.takeIf { !it.isEmpty } ?: return
        item.editMeta { it.setEnchantmentGlintOverride(if (glow) true else null) }
        inv.setItemInMainHand(item)
    }
}