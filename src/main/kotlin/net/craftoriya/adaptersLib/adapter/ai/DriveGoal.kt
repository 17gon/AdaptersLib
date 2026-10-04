package net.craftoriya.adaptersLib.adapter.ai

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Mob
import org.bukkit.persistence.PersistentDataType
import java.util.EnumSet

class DriveGoal(
    private val mob: Mob
) : Goal<Mob> {
    private val pdc get() = mob.persistentDataContainer
    private val key = GoalKey.of(Mob::class.java, NamespacedKey(AiKeys.plugin, "drive_${mob.type.key}"))

    override fun shouldActivate(): Boolean =
        pdc.getOrDefault(AiKeys.CONTROLLED, PersistentDataType.BYTE, 0) == 1.toByte() &&
                pdc.has(AiKeys.TARGET_X, PersistentDataType.DOUBLE)

    override fun shouldStayActive() = shouldActivate()

    override fun start() { applyTarget() }
    override fun tick() { applyTarget() }

    private fun applyTarget() {
        val x = pdc.get(AiKeys.TARGET_X, PersistentDataType.DOUBLE) ?: return
        val y = pdc.get(AiKeys.TARGET_Y, PersistentDataType.DOUBLE) ?: return
        val z = pdc.get(AiKeys.TARGET_Z, PersistentDataType.DOUBLE) ?: return
        val speed = pdc.getOrDefault(AiKeys.SPEED, PersistentDataType.DOUBLE, 0.5)
        mob.pathfinder.moveTo(Location(mob.world, x, y, z), speed)
    }

    override fun stop() { mob.pathfinder.stopPathfinding() }
    override fun getKey() = key
    override fun getTypes(): EnumSet<GoalType> = EnumSet.of(GoalType.MOVE, GoalType.LOOK)
}