package net.craftoriya.adaptersLib.model

data class OutlineMarker(
    val world: String,
    val min: Vec3I,
    val max: Vec3I,
    val style: OutlineStyle
)