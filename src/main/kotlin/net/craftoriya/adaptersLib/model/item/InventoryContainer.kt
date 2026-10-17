package net.craftoriya.adaptersLib.model.item

import net.craftoriya.adaptersLib.model.data.DataContainer
import net.craftoriya.adaptersLib.model.math.LocationIContainer


data class InventoryContainer(
    val type: InventoryTypeDomain,
    val items: List<ItemContainer?>,
    val location: LocationIContainer? = null,
    val data: DataContainer = emptyMap()
)