package net.craftoriya.adaptersLib.port.world

import net.craftoriya.adaptersLib.model.block.BlockContainer
import net.craftoriya.adaptersLib.model.block.BlockInfo
import net.craftoriya.adaptersLib.model.math.LocationIContainer

interface IWorldQueryPort {
    fun isLoaded(at: LocationIContainer): Boolean
    fun blockAt(at: LocationIContainer): BlockContainer?
    fun findBlocks(center: LocationIContainer, radius: Int, materials: Set<String>): List<BlockContainer>
    fun info(at: LocationIContainer): BlockInfo?
    fun isFullBlock(material: String): Boolean
    fun isBlock(material: String): Boolean
    fun canPlace(material: String): Boolean
}