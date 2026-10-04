package net.craftoriya.adaptersLib.adapter

import net.craftoriya.adaptersLib.port.IOutlinePort
import net.craftoriya.adaptersLib.port.OutlineMarker
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.BlockDisplay
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import java.util.UUID

class BukkitOutlineAdapter(private val plugin: Plugin) : IOutlinePort {
    private class View(val displays: List<BlockDisplay>, val task: BukkitTask)
    private class Bar(val x: Float, val y: Float, val z: Float, val sx: Float, val sy: Float, val sz: Float)

    private val active = mutableMapOf<UUID, View>()

    override fun show(playerId: UUID, markers: List<OutlineMarker>, ticks: Int) {
        clear(playerId)
        val player = Bukkit.getPlayer(playerId) ?: return

        val displays = ArrayList<BlockDisplay>()
        for (m in markers) {
            val world = Bukkit.getWorld(m.location.world) ?: continue
            val v = m.location.vec3I
            val origin = Location(world, v.x.toDouble(), v.y.toDouble(), v.z.toDouble())
            val color = Color.fromRGB(m.rgb and 0xFFFFFF)
            for (bar in BARS) {
                displays += world.spawn(origin, BlockDisplay::class.java) { d ->
                    d.isVisibleByDefault = false
                    d.isPersistent = false
                    d.block = Material.BLACK_CONCRETE.createBlockData()
                    d.isGlowing = true
                    d.glowColorOverride = color
                    d.transformation = Transformation(
                        Vector3f(bar.x, bar.y, bar.z), AxisAngle4f(),
                        Vector3f(bar.sx, bar.sy, bar.sz), AxisAngle4f()
                    )
                }
            }
        }
        displays.forEach { player.showEntity(plugin, it) }

        val task = Bukkit.getScheduler().runTaskLater(plugin, Runnable { clear(playerId) }, ticks.toLong())
        active[playerId] = View(displays, task)
    }

    override fun clear(playerId: UUID) {
        val view = active.remove(playerId) ?: return
        view.task.cancel()
        view.displays.forEach { it.remove() }
    }

    fun clearAll() = active.keys.toList().forEach { clear(it) }

    private companion object {
        const val THICKNESS = 0.04f
        const val OUTSET = 0.005f
        const val LENGTH = 1f + 2 * OUTSET

        val BARS: List<Bar> = buildList {
            val ends = floatArrayOf(-OUTSET, 1f + OUTSET - THICKNESS)
            for (a in ends) for (b in ends) {
                add(Bar(-OUTSET, a, b, LENGTH, THICKNESS, THICKNESS))
                add(Bar(a, -OUTSET, b, THICKNESS, LENGTH, THICKNESS))
                add(Bar(a, b, -OUTSET, THICKNESS, THICKNESS, LENGTH))
            }
        }
    }
}