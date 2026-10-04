package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.EntityContainer
import net.craftoriya.adaptersLib.model.Vec3D
import java.util.*

interface IVillagerControlPort {
    fun setControlled(id: UUID, controlled: Boolean)
    fun setHardControlled(id: UUID, hard: Boolean)
    fun setTarget(id: UUID, target: Vec3D?, speed: Double = 0.5)
    fun lookAt(id: UUID, target: Vec3D)
    fun faceTo(id: UUID, target: Vec3D)
    fun swingMainHand(id: UUID)
    fun isControlled(id: UUID): Boolean
    fun isHardControlled(id: UUID): Boolean
    fun getNearbyEntities(center: Vec3D, radius: Double, worldName: String): List<EntityContainer>
}