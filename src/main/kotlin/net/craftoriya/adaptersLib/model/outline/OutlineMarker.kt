package net.craftoriya.adaptersLib.model.outline

import net.craftoriya.adaptersLib.model.math.Vec3I

data class OutlineMarker(
    val world: String,
    val min: Vec3I,
    val max: Vec3I,
    val style: OutlineStyle
)