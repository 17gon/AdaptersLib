package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.ItemContainer
import net.craftoriya.adaptersLib.model.ItemSource
import net.craftoriya.adaptersLib.model.LocationIContainer
import net.craftoriya.adaptersLib.model.SlotItem
import net.craftoriya.adaptersLib.model.Vec3D
import java.util.UUID

interface IEntityPort {
    fun position(id: UUID): Vec3D?
    fun mainHand(id: UUID): ItemContainer?
    fun inventory(id: UUID): List<SlotItem>
    fun pull(id: UUID, from: LocationIContainer, slot: Int): Int?
    fun push(id: UUID, slot: Int, to: LocationIContainer): Boolean
    fun display(id: UUID, source: ItemSource?)
}