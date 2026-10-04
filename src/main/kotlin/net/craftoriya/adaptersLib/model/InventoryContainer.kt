package net.craftoriya.adaptersLib.model


data class InventoryContainer(
    val type: InventoryTypeDomain,
    val items: List<ItemContainer?>,
    val location: LocationIContainer? = null,
    val data: DataContainer = emptyMap()
)