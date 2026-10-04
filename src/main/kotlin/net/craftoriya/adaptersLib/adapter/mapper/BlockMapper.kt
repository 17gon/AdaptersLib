package net.craftoriya.adaptersLib.adapter.mapper

import net.craftoriya.adaptersLib.model.BlockContainer
import net.craftoriya.adaptersLib.model.LocationIContainer
import net.craftoriya.adaptersLib.model.Vec3I
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.block.DoubleChest

internal object BlockMapper {

    fun pos(b: Block) = LocationIContainer(b.world.name, Vec3I(b.x, b.y, b.z))

    fun normalize(block: Block): Block {
        if (block.type != Material.CHEST && block.type != Material.TRAPPED_CHEST) return block
        val state = block.getState(false)
        if (state is Chest) {
            val holder = state.inventory.holder
            if (holder is DoubleChest) (holder.leftSide as? Chest)?.let { return it.block }
        }
        return block
    }

    fun toContainer(block: Block): BlockContainer =
        BlockContainer(pos(block), pos(normalize(block)), block.type.name)
}