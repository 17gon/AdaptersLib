package net.craftoriya.adaptersLib.port.entity

import net.craftoriya.adaptersLib.model.item.ItemContainer
import net.craftoriya.adaptersLib.model.item.ItemSource
import net.craftoriya.adaptersLib.model.math.LocationIContainer
import net.craftoriya.adaptersLib.model.item.SlotItem
import net.craftoriya.adaptersLib.model.math.Vec3D
import java.util.UUID

interface IEntityPort {
    fun position(id: UUID): Vec3D?
    fun mainHand(id: UUID): ItemContainer?
    fun inventory(id: UUID): List<SlotItem>
    fun pull(id: UUID, from: LocationIContainer, slot: Int): Int?
    fun push(id: UUID, slot: Int, to: LocationIContainer): Boolean
    fun display(id: UUID, source: ItemSource?)
}