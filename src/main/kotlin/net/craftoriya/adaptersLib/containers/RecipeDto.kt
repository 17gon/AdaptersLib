package net.craftoriya.adaptersLib.containers

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class RecipeDto(
    val output: String = "",
    val count: Int = 1,
    val shape: List<String> = emptyList(),
    val key: Map<String, String> = emptyMap()
)