package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.BreakResult
import net.craftoriya.adaptersLib.model.ItemSource
import net.craftoriya.adaptersLib.model.LocationIContainer
import java.util.UUID

interface IBlockBreakPort {
    fun breakTicks(breaker: UUID, at: LocationIContainer, tool: ItemSource = ItemSource.HAND): Int?
    fun breakBlock(breaker: UUID, at: LocationIContainer, tool: ItemSource = ItemSource.HAND): BreakResult
}