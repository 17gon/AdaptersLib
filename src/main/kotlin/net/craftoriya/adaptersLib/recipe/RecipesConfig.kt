package net.craftoriya.adaptersLib.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class RecipesConfig(
    val shaped: List<RecipeShapedDto> = emptyList(),
    val shapeless: List<RecipeShapelessDto> = emptyList(),
    val smelting: List<RecipeSmeltingDto> = emptyList(),
    val trades: List<RecipeTradesDto> = emptyList(),
)