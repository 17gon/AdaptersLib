package net.craftoriya.adaptersLib.model

data class BlockInfo(
    val material: String,
    val hardness: Float,
    val solid: Boolean,
    val passable: Boolean,
    val liquid: Boolean,
    val waterlogged: Boolean,
    val fullBlock: Boolean,
    val hazard: Boolean,
    val falling: Boolean,
)