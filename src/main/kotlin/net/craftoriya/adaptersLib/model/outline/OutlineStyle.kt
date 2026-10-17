package net.craftoriya.adaptersLib.model.outline

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class OutlineStyle(
    val shape: OutlineShape = OutlineShape.WIREFRAME,
    val display: OutlineDisplay = OutlineDisplay.BLOCK,
    val material: String = "WHITE_CONCRETE",
    val rgb: Int = 0xFFFFFF,
    val glow: Boolean = true,
    val tint: Boolean = false,
    val brightness: Int = 15,
    val thickness: Float = 0.04f,
    val outset: Float = 0.005f,
    val arm: Float = 0.3f
)