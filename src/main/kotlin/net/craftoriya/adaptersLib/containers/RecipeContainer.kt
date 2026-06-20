package net.craftoriya.adaptersLib.containers

sealed interface RecipeContainer {
    val output: ItemContainer

    data class Shaped(
        override val output: ItemContainer,
        val pattern: List<ItemContainer?> // 9 slots, null = air
    ) : RecipeContainer

    data class Shapeless(
        override val output: ItemContainer,
        val ingredients: List<ItemContainer>
    ) : RecipeContainer
}
