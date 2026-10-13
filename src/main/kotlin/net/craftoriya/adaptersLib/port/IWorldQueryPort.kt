package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.BlockContainer
import net.craftoriya.adaptersLib.model.BlockInfo
import net.craftoriya.adaptersLib.model.LocationIContainer

interface IWorldQueryPort {
    fun isLoaded(at: LocationIContainer): Boolean
    fun blockAt(at: LocationIContainer): BlockContainer?
    fun findBlocks(center: LocationIContainer, radius: Int, materials: Set<String>): List<BlockContainer>
    fun info(at: LocationIContainer): BlockInfo?
    fun isFullBlock(material: String): Boolean
}