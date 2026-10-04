package net.craftoriya.adaptersLib.model

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ItemContainer (
    val name: String = "",
    val material: String = "",
    val count: Int = 0,
    val properties: Map<String, String> = mutableMapOf(),
)