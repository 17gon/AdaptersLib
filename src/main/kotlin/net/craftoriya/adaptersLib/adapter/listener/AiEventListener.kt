package net.craftoriya.adaptersLib.adapter.listener

import net.craftoriya.adaptersLib.adapter.ai.AiKeys
import net.craftoriya.adaptersLib.adapter.ai.DriveGoal
import net.craftoriya.adaptersLib.event.DomainEventBus
import org.bukkit.Bukkit
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.persistence.PersistentDataType

class AiEventListener(private val bus: DomainEventBus): Listener {
    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        event.chunk.entities.filterIsInstance<Mob>().forEach { applyGoal(it) }
    }

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        (event.entity as? Mob)?.let { applyGoal(it) }
    }

    private fun applyGoal(mob: Mob) {
        val hard = mob.persistentDataContainer
            .getOrDefault(AiKeys.HARD, PersistentDataType.BYTE, 0) == 1.toByte()

        if (hard) Bukkit.getMobGoals().removeAllGoals(mob)

        val goal = DriveGoal(mob)
        Bukkit.getMobGoals().addGoal(mob, 0, goal)
    }
}