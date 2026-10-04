package net.craftoriya.adaptersLib.adapter

import net.craftoriya.adaptersLib.adapter.mapper.BlockMapper
import net.craftoriya.adaptersLib.model.BlockContainer
import net.craftoriya.adaptersLib.model.LocationIContainer
import net.craftoriya.adaptersLib.port.IWorldQueryPort
import org.bukkit.Bukkit
import org.bukkit.Material

class BukkitWorldQueryAdapter : IWorldQueryPort {

    override fun isLoaded(at: LocationIContainer): Boolean {
        val world = Bukkit.getWorld(at.world) ?: return false
        return world.isChunkLoaded(at.vec3I.x shr 4, at.vec3I.z shr 4)
    }

    override fun blockAt(at: LocationIContainer): BlockContainer? {
        val world = Bukkit.getWorld(at.world) ?: return null
        val v = at.vec3I
        if (!world.isChunkLoaded(v.x shr 4, v.z shr 4)) return null
        return BlockMapper.toContainer(world.getBlockAt(v.x, v.y, v.z))
    }

    override fun findBlocks(center: LocationIContainer, radius: Int, materials: Set<String>): List<BlockContainer> {
        val world = Bukkit.getWorld(center.world) ?: return emptyList()
        val wanted = materials.mapNotNull { Material.matchMaterial(it) }.toSet()
        if (wanted.isEmpty()) return emptyList()
        val c = center.vec3I
        val out = ArrayList<BlockContainer>()
        for (x in c.x - radius..c.x + radius) for (z in c.z - radius..c.z + radius) {
            if (!world.isChunkLoaded(x shr 4, z shr 4)) continue
            for (y in c.y - radius..c.y + radius) {
                if (world.getType(x, y, z) in wanted) out += BlockMapper.toContainer(world.getBlockAt(x, y, z))
            }
        }
        return out
    }
}