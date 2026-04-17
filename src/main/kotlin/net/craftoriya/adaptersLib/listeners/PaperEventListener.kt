package net.craftoriya.adaptersLib.listeners

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import jdk.internal.vm.ThreadContainers.container
import net.craftoriya.adaptersLib.containers.CraftingGridContainer
import net.craftoriya.adaptersLib.containers.InventoryTypeDomain
import net.craftoriya.adaptersLib.containers.ItemContainer
import net.craftoriya.adaptersLib.containers.PlayerContainer
import net.craftoriya.adaptersLib.containers.Vec3D
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.events.DomainCraftingCompleteEvent
import net.craftoriya.adaptersLib.event.events.DomainPrepareItemCraftEvent
import net.craftoriya.adaptersLib.event.events.DomainPlayerJumpEvent
import net.craftoriya.adaptersLib.mappers.ItemStackMapper
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class PaperEventListener(private val bus: DomainEventBus): Listener {
    private val itemMapper: ItemStackMapper = ItemStackMapper

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player
        val pos = Vec3D(player.location.x, player.location.y, player.location.z)
        val playerContainer = PlayerContainer(player.uniqueId, player.name, pos, player.isOnGround)
        val domainEvent = DomainPlayerJumpEvent(playerContainer)

        bus.publish(domainEvent)
        if (domainEvent.isCancelled) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPrepareItemCraftEvent(event: PrepareItemCraftEvent) {
        val items = event.inventory.map { item ->
            if (item == null || item.type.isAir) return@map null
            else buildItemContainer(item)
        }

        val type: InventoryTypeDomain = when (event.inventory.type) {
            InventoryType.WORKBENCH -> InventoryTypeDomain.WORKBENCH
            InventoryType.CRAFTING -> InventoryTypeDomain.CRAFTING
            else -> return
        }
        val grid = CraftingGridContainer(type, items)
        val domainEvent = DomainPrepareItemCraftEvent(grid, event.isRepair)
        bus.publish(domainEvent)
        if (domainEvent.result != null) {
            event.inventory.result = itemMapper.toItemStack(domainEvent.result!!)
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onCraftItemEvent(event: CraftItemEvent) {
        val items = event.inventory.map { item ->
            if (item == null || item.type.isAir) return@map null
            else buildItemContainer(item)
        }
        val type: InventoryTypeDomain = when (event.inventory.type) {
            InventoryType.WORKBENCH -> InventoryTypeDomain.WORKBENCH
            InventoryType.CRAFTING -> InventoryTypeDomain.CRAFTING
            else -> return
        }
        val grid = CraftingGridContainer(type, items)
        val domainEvent = DomainCraftingCompleteEvent(grid)
        bus.publish(domainEvent)
        if (domainEvent.isCancelled) event.isCancelled = true
    }

    private fun buildItemContainer(item: ItemStack): ItemContainer {
        val data = mutableMapOf<String, String>()
        item.itemMeta?.let { meta ->
            val container = meta.persistentDataContainer
            container.keys.forEach { key ->
                val value: String? = when {
                    container.has(key, PersistentDataType.STRING) -> container.get(key, PersistentDataType.STRING)
                    container.has(key, PersistentDataType.INTEGER) -> container.get(key, PersistentDataType.INTEGER)?.toString()
                    container.has(key, PersistentDataType.DOUBLE) -> container.get(key, PersistentDataType.DOUBLE)?.toString()
                    container.has(key, PersistentDataType.LONG) -> container.get(key, PersistentDataType.LONG)?.toString()
                    container.has(key, PersistentDataType.BYTE) -> container.get(key, PersistentDataType.BYTE)?.toString()
                    else -> null
                }
                if (value != null) data[key.key] = value
            }
        }
        return ItemContainer(
            item.itemMeta?.displayName()?.toString() ?: "",
            item.type.toString(),
            item.amount,
            data
        )
    }
}