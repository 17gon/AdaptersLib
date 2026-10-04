package net.craftoriya.adaptersLib.model

data class CraftingGridContainer (
    val type: InventoryTypeDomain, // WORKBENCH(10) or CRAFTING(5)
    val items: List<ItemContainer?>
)