package net.craftoriya.adaptersLib.adapter.world

import net.craftoriya.adaptersLib.model.math.LocationIContainer
import net.craftoriya.adaptersLib.model.math.Vec3I
import net.craftoriya.adaptersLib.port.world.IBlockDataPort
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class BukkitBlockDataAdapter(private val plugin: Plugin) : IBlockDataPort {

    override fun get(at: LocationIContainer, key: String): String? =
        pdc(at)?.get(nsKey(key, at.vec3I), PersistentDataType.STRING)

    override fun set(at: LocationIContainer, key: String, value: String) {
        pdc(at)?.set(nsKey(key, at.vec3I), PersistentDataType.STRING, value)
    }

    override fun remove(at: LocationIContainer, key: String) {
        pdc(at)?.remove(nsKey(key, at.vec3I))
    }

    private fun pdc(at: LocationIContainer): PersistentDataContainer? {
        val world = Bukkit.getWorld(at.world) ?: return null
        val cx = at.vec3I.x shr 4 //basically the same as at/16, but faster (?)
        val cz = at.vec3I.z shr 4
        if (!world.isChunkLoaded(cx, cz)) return null
        return world.getChunkAt(cx, cz).persistentDataContainer
    }

    private fun nsKey(key: String, v: Vec3I): NamespacedKey {
        val packed = ((v.x.toLong() and 0x3FFFFFF) shl 38) or
                ((v.z.toLong() and 0x3FFFFFF) shl 12) or
                (v.y.toLong() and 0xFFF)

        return NamespacedKey(plugin, "$key${packed.toULong().toString(16)}") // 0o0
    }
}