package net.craftoriya.adaptersLib.adapter.listener

import net.craftoriya.adaptersLib.model.EntityContainer
import net.craftoriya.adaptersLib.model.LocationIContainer
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.domainevents.DomainVillagerTickEvent
import net.craftoriya.adaptersLib.port.IVillagerSnapshotPort
import net.craftoriya.adaptersLib.adapter.mapper.ProfessionMapper
import net.craftoriya.adaptersLib.model.Vec3I
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Villager
import org.bukkit.entity.memory.MemoryKey
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import kotlin.math.sqrt

class VillagerTickSource(
    private val plugin: Plugin,
    private val bus: DomainEventBus,
    private val intervalTicks: Long = 40L
) : IVillagerSnapshotPort {
    private var task: BukkitTask? = null

    fun start() {
        if (task != null) return
        task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable { tick() }, intervalTicks, intervalTicks)
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    override fun snapshot(): List<DomainVillagerTickEvent> {
        val out = ArrayList<DomainVillagerTickEvent>()
        forEachVillager { out += it }
        return out
    }

    private fun tick() = forEachVillager { bus.publish(it) }

    private inline fun forEachVillager(visit: (DomainVillagerTickEvent) -> Unit) {
        val now = Bukkit.getCurrentTick().toLong()
        for (world in Bukkit.getWorlds()) {
            for (v in world.getEntitiesByClass(Villager::class.java)) visit(toEvent(world, v, now))
        }
    }

    private fun toEvent(world: World, villager: Villager, now: Long): DomainVillagerTickEvent {
        val site = villager.getMemory(MemoryKey.JOB_SITE)?.takeIf { it.world == world }
        val distance = site?.let { sqrt(villager.location.distanceSquared(it)) } ?: Double.MAX_VALUE
        val loc = villager.location
        return DomainVillagerTickEvent(
            EntityContainer(villager.uniqueId, "VILLAGER", villager.name),
            ProfessionMapper.map(villager.profession),
            villager.villagerLevel,
            LocationIContainer(world.name, Vec3I(loc.blockX, loc.blockY, loc.blockZ)),
            site?.let { LocationIContainer(world.name, Vec3I(it.blockX, it.blockY, it.blockZ)) },
            distance,
            villager.isSleeping,
            world.time,
            now
        )
    }
}