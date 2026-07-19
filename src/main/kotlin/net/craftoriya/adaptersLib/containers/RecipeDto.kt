package net.craftoriya.adaptersLib.containers

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class RecipeShapedDto(
    val output: String = "",
    val count: Int = 1,
    val shape: List<String> = emptyList(),
    val key: Map<String, String> = emptyMap()
)
@ConfigSerializable
data class RecipeShapelessDto(
    val output: String = "",
    val count: Int = 1,
    val ingredients: List<String> = emptyList()
)

@ConfigSerializable
data class RecipeSmeltingDto(
    val input: String = "",
    val output: String = "",
    val count: Int = 1,
    val cookingTime: Int = 200,
    val experience: Float = 0f,
    val types: List<RecipeContainer.CookingType> = emptyList(),
    val intake: Int = 1
)