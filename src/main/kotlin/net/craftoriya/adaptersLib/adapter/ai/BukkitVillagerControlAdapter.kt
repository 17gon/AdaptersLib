package net.craftoriya.adaptersLib.adapter.ai

import net.craftoriya.adaptersLib.model.EntityContainer
import net.craftoriya.adaptersLib.model.Vec3D
import net.craftoriya.adaptersLib.port.IVillagerControlPort
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Mob
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.sqrt

class BukkitVillagerControlAdapter : IVillagerControlPort {

    override fun setControlled(id: UUID, controlled: Boolean) {
        val pdc = mob(id)?.persistentDataContainer ?: return
        pdc.set(AiKeys.CONTROLLED, PersistentDataType.BYTE, if (controlled) 1 else 0)
    }

    override fun setHardControlled(id: UUID, hard: Boolean) {
        val m = mob(id) ?: return
        val pdc = m.persistentDataContainer
        val wasHard = pdc.getOrDefault(AiKeys.HARD, PersistentDataType.BYTE, 0) == 1.toByte()
        pdc.set(AiKeys.HARD, PersistentDataType.BYTE, if (hard) 1 else 0)

        if (hard && !wasHard) {
            Bukkit.getMobGoals().removeAllGoals(m)
            Bukkit.getMobGoals().addGoal(m, 0, DriveGoal(m))
        }
    }

    override fun setTarget(id: UUID, target: Vec3D?, speed: Double) {
        val pdc = mob(id)?.persistentDataContainer ?: return
        if (target == null) {
            pdc.remove(AiKeys.TARGET_X); pdc.remove(AiKeys.TARGET_Y); pdc.remove(AiKeys.TARGET_Z)
        } else {
            pdc.set(AiKeys.TARGET_X, PersistentDataType.DOUBLE, target.x)
            pdc.set(AiKeys.TARGET_Y, PersistentDataType.DOUBLE, target.y)
            pdc.set(AiKeys.TARGET_Z, PersistentDataType.DOUBLE, target.z)
            pdc.set(AiKeys.SPEED, PersistentDataType.DOUBLE, speed)
        }
    }

    override fun lookAt(id: UUID, target: Vec3D) {
        mob(id)?.lookAt(target.x, target.y, target.z)
    }

    override fun faceTo(id: UUID, target: Vec3D) {
        val mob = mob(id) ?: return
        val eye = mob.eyeLocation
        val dx = target.x - eye.x
        val dy = target.y - eye.y
        val dz = target.z - eye.z
        val yaw = Math.toDegrees(atan2(-dx, dz)).toFloat()
        val pitch = Math.toDegrees(-atan2(dy, sqrt(dx * dx + dz * dz))).toFloat()
        mob.setRotation(yaw, pitch)
        mob.lookAt(target.x, target.y, target.z)
    }

    override fun swingMainHand(id: UUID) {
        mob(id)?.swingMainHand()
    }

    override fun isControlled(id: UUID): Boolean =
        mob(id)?.persistentDataContainer?.getOrDefault(AiKeys.CONTROLLED, PersistentDataType.BYTE, 0) == 1.toByte()

    override fun isHardControlled(id: UUID): Boolean =
        mob(id)?.persistentDataContainer?.getOrDefault(AiKeys.HARD, PersistentDataType.BYTE, 0) == 1.toByte()

    override fun getNearbyEntities(center: Vec3D, radius: Double, worldName: String): List<EntityContainer> {
        val world = Bukkit.getWorld(worldName) ?: return emptyList()
        val loc = Location(world, center.x, center.y, center.z)
        return world.getNearbyEntities(loc, radius, radius, radius).map {
            EntityContainer(it.uniqueId, it.type.name, it.name)
        }
    }

    private fun mob(id: UUID): Mob? = Bukkit.getEntity(id) as? Mob
}