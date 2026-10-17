package net.craftoriya.adaptersLib.port.world

import net.craftoriya.adaptersLib.model.block.BreakResult
import net.craftoriya.adaptersLib.model.item.ItemContainer
import net.craftoriya.adaptersLib.model.item.ItemSource
import net.craftoriya.adaptersLib.model.math.LocationIContainer
import java.util.UUID

interface IBlockBreakPort {
    fun breakTicks(breaker: UUID, at: LocationIContainer, tool: ItemSource = ItemSource.HAND): Int?
    fun breakBlock(breaker: UUID, at: LocationIContainer, tool: ItemSource = ItemSource.HAND): BreakResult
    fun scoop(breaker: UUID, at: LocationIContainer, bucket: ItemSource): ItemContainer?
    fun place(at: LocationIContainer, material: String): Boolean
}