package net.craftoriya.adaptersLib.model.block

import net.craftoriya.adaptersLib.model.math.LocationIContainer

data class BlockContainer(
    val location: LocationIContainer,
    val anchor: LocationIContainer,
    val material: String
)