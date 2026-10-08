package net.craftoriya.adaptersLib.model

data class MenuContainer(
    val title: String,
    val rows: Int,
    val items: Map<Int, ItemContainer>
)