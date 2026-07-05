package net.craftoriya.adaptersLib.containers

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class RecipesConfig(val recipes: List<RecipeDto> = emptyList())
