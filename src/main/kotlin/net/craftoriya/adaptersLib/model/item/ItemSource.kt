package net.craftoriya.adaptersLib.model.item

import net.craftoriya.adaptersLib.model.math.LocationIContainer
import java.util.UUID

data class ItemSource(
    val container: LocationIContainer? = null,
    val slot: Int = 0,
    val entity: UUID? = null
) {
    companion object {
        val HAND = ItemSource()
        fun inventoryOf(entity: UUID, slot: Int) = ItemSource(null, slot, entity)
    }
}