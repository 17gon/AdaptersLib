package net.craftoriya.adaptersLib.adapter.listener

import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.domainevents.BlockChange
import net.craftoriya.adaptersLib.event.domainevents.DomainBlockChangeEvent
import net.craftoriya.adaptersLib.adapter.mapper.BlockMapper
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent

class BlockSyncListener(private val bus: DomainEventBus) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) = publishEvent(event.block, BlockChange.BREAK)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) = publishEvent(event.block, BlockChange.PLACE)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onExplode(event: EntityExplodeEvent) = event.blockList().forEach { publishEvent(it, BlockChange.BREAK) }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockExplode(event: BlockExplodeEvent) = event.blockList().forEach { publishEvent(it, BlockChange.BREAK) }

    private fun publishEvent(block: Block, change: BlockChange) =
        bus.publish(DomainBlockChangeEvent(BlockMapper.toContainer(block), change))
}