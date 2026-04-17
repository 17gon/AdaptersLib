package net.craftoriya.adaptersLib.containers

data class CraftingGridContainer (
    val type: InventoryTypeDomain, // WORKBENCH(10) or CRAFTING(5)
    val items: List<ItemContainer?>
)