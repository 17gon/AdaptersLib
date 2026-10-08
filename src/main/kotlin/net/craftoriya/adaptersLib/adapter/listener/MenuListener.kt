package net.craftoriya.adaptersLib.adapter.listener

import net.craftoriya.adaptersLib.adapter.MenuHolder
import net.craftoriya.adaptersLib.adapter.mapper.PlayerMapper
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.domainevents.DomainMenuClickEvent
import net.craftoriya.adaptersLib.event.domainevents.DomainMenuCloseEvent
import net.craftoriya.adaptersLib.model.MenuClick
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class MenuListener(private val bus: DomainEventBus) : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onClick(event: InventoryClickEvent) {
        val holder = event.view.topInventory.holder as? MenuHolder ?: return
        event.isCancelled = true
        if (event.clickedInventory !== event.view.topInventory) return
        val player = event.whoClicked as? Player ?: return
        val click = when (event.click) {
            ClickType.LEFT -> MenuClick.LEFT
            ClickType.RIGHT -> MenuClick.RIGHT
            ClickType.SHIFT_LEFT -> MenuClick.SHIFT_LEFT
            ClickType.SHIFT_RIGHT -> MenuClick.SHIFT_RIGHT
            ClickType.MIDDLE -> MenuClick.MIDDLE
            else -> MenuClick.OTHER
        }
        bus.publish(DomainMenuClickEvent(PlayerMapper.toContainer(player), holder.menuId, event.slot, click))
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDrag(event: InventoryDragEvent) {
        if (event.view.topInventory.holder is MenuHolder) event.isCancelled = true
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder as? MenuHolder ?: return
        val player = event.player as? Player ?: return
        bus.publish(DomainMenuCloseEvent(PlayerMapper.toContainer(player), holder.menuId))
    }
}