package net.craftoriya.adaptersLib.adapter

import net.craftoriya.adaptersLib.adapter.mapper.ItemStackMapper
import net.craftoriya.adaptersLib.model.MenuContainer
import net.craftoriya.adaptersLib.port.IMenuPort
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import java.util.UUID

class BukkitMenuAdapter : IMenuPort {

    override fun open(id: UUID, menuId: String, menu: MenuContainer) {
        val player = Bukkit.getPlayer(id) ?: return
        val holder = MenuHolder(menuId)
        val inventory = Bukkit.createInventory(holder, (menu.rows.coerceIn(1, 6) * 9), MiniMessage.miniMessage().deserialize(menu.title))
        holder.backing = inventory
        fill(inventory, menu)
        player.openInventory(inventory)
    }

    override fun refresh(id: UUID, menu: MenuContainer) {
        val top = Bukkit.getPlayer(id)?.openInventory?.topInventory ?: return
        if (top.holder !is MenuHolder) return
        top.clear()
        fill(top, menu)
    }

    override fun close(id: UUID) {
        val player = Bukkit.getPlayer(id) ?: return
        if (player.openInventory.topInventory.holder is MenuHolder) player.closeInventory()
    }

    private fun fill(inventory: Inventory, menu: MenuContainer) {
        menu.items.forEach { (slot, item) ->
            if (slot in 0 until inventory.size) inventory.setItem(slot, ItemStackMapper.toItemStack(item))
        }
    }
}