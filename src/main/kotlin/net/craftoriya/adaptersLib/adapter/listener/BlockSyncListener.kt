package net.craftoriya.adaptersLib.adapter.listener

import net.craftoriya.adaptersLib.adapter.mapper.BlockMapper
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.domainevents.block.DomainBlockChangeEvent
import net.craftoriya.adaptersLib.model.block.BlockChange
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.world.StructureGrowEvent

class BlockSyncListener(private val bus: DomainEventBus) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) = publishEvent(event.block, BlockChange.BREAK)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) = publishEvent(event.block, BlockChange.PLACE)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onExplode(event: EntityExplodeEvent) = event.blockList().forEach { publishEvent(it, BlockChange.BREAK) }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockExplode(event: BlockExplodeEvent) = event.blockList().forEach { publishEvent(it, BlockChange.BREAK) }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onForm(event: BlockFormEvent) = publishEvent(event.block, BlockChange.FORM)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onGrow(event: BlockGrowEvent) = publishEvent(event.block, BlockChange.GROW)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onStructureGrow(event: StructureGrowEvent) = event.blocks.forEach { publishEvent(it.block, BlockChange.GROW) }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPistonExtend(event: BlockPistonExtendEvent) =
        event.blocks.forEach { publishEvent(it.getRelative(event.direction), BlockChange.MOVE) }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPistonRetract(event: BlockPistonRetractEvent) = event.blocks.forEach {
        publishEvent(it.getRelative(event.direction), BlockChange.MOVE)
        publishEvent(it.getRelative(event.direction.oppositeFace), BlockChange.MOVE)
    }

    private fun publishEvent(block: Block, change: BlockChange) =
        bus.publish(DomainBlockChangeEvent(BlockMapper.toContainer(block), change))
}