package net.craftoriya.adaptersLib.containers

data class ItemContainer (
    val name: String,
    val material: String,
    val count: Int,
    val properties: Map<String, String>
)