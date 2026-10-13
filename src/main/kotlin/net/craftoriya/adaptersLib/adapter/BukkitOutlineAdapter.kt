package net.craftoriya.adaptersLib.adapter

import net.craftoriya.adaptersLib.model.OutlineDisplay
import net.craftoriya.adaptersLib.model.OutlineMarker
import net.craftoriya.adaptersLib.model.OutlineStyle
import net.craftoriya.adaptersLib.outline.OutlineGeometry
import net.craftoriya.adaptersLib.port.IOutlinePort
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import java.util.UUID

class BukkitOutlineAdapter(private val plugin: Plugin) : IOutlinePort {
    private class View(val displays: List<Display>, val task: BukkitTask)

    private val active = mutableMapOf<UUID, View>()

    override fun show(playerId: UUID, markers: List<OutlineMarker>, ticks: Int) {
        clear(playerId)
        val player = Bukkit.getPlayer(playerId) ?: return

        val displays = ArrayList<Display>()
        for (m: OutlineMarker in markers) {
            val world = Bukkit.getWorld(m.world) ?: continue
            val parts = OutlineGeometry.parts(m.min, m.max, m.style)
            if (parts.isEmpty()) continue

            val origin = Location(world, (m.min.x + m.max.x + 1) / 2.0, m.min.y.toDouble(), (m.min.z + m.max.z + 1) / 2.0)
            val width = (maxOf(m.max.x - m.min.x, m.max.z - m.min.z) + 3).toFloat()
            val height = (m.max.y - m.min.y + 3).toFloat()
            for (p: OutlineGeometry.Part in parts) displays += spawn(world, origin, p, m.style, width, height)
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

    private fun spawn(world: World, origin: Location, part: OutlineGeometry.Part, style: OutlineStyle, w: Float, h: Float): Display {
        val sx: Float = part.sx.toFloat()
        val sy: Float = part.sy.toFloat()
        val sz: Float = part.sz.toFloat()
        val ox: Float = (part.x - origin.x).toFloat()
        val oy: Float = (part.y - origin.y).toFloat()
        val oz: Float = (part.z - origin.z).toFloat()

        return if (style.display == OutlineDisplay.ITEM) {
            world.spawn(origin, ItemDisplay::class.java) { display ->
                display.setItemStack(item(style))
                display.itemDisplayTransform = ItemDisplay.ItemDisplayTransform.NONE
                common(display, style, w, h, Vector3f(ox + sx / 2, oy + sy / 2, oz + sz / 2), Vector3f(sx, sy, sz))
            }
        } else {
            val data = blockData(style)
            world.spawn(origin, BlockDisplay::class.java) { display ->
                display.block = data
                common(display, style, w, h, Vector3f(ox, oy, oz), Vector3f(sx, sy, sz))
            }
        }
    }

    private fun common(display: Display, style: OutlineStyle, w: Float, h: Float, translation: Vector3f, scale: Vector3f) {
        display.isVisibleByDefault = false
        display.isPersistent = false
        display.viewRange = 2f
        display.displayWidth = w
        display.displayHeight = h
        if (style.brightness in 0..15) display.brightness = Display.Brightness(style.brightness, style.brightness)
        if (style.glow) {
            display.isGlowing = true
            display.glowColorOverride = Color.fromRGB(style.rgb and 0xFFFFFF)
        }
        display.transformation = Transformation(translation, AxisAngle4f(), scale, AxisAngle4f())
    }

    private fun blockData(style: OutlineStyle): BlockData {
        val material: Material = Material.matchMaterial(style.material)?.takeIf { it.isBlock } ?: Material.WHITE_CONCRETE
        return material.createBlockData()
    }

    private fun item(style: OutlineStyle): ItemStack {
        val material: Material = Material.matchMaterial(style.material)?.takeIf { it.isItem } ?: Material.WHITE_CONCRETE
        val stack = ItemStack(material)
        if (style.tint) {
            val meta: ItemMeta? = stack.itemMeta
            if (meta is LeatherArmorMeta) {
                meta.setColor(Color.fromRGB(style.rgb and 0xFFFFFF))
                stack.itemMeta = meta
            }
        }
        return stack
    }
}