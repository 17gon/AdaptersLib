package net.craftoriya.adaptersLib.model.menu

import net.craftoriya.adaptersLib.model.item.ItemContainer

data class MenuContainer(
    val title: String,
    val rows: Int,
    val items: Map<Int, ItemContainer>
)