package net.craftoriya.adaptersLib.adapter.world

import net.craftoriya.adaptersLib.adapter.mapper.BlockMapper
import net.craftoriya.adaptersLib.model.block.BlockContainer
import net.craftoriya.adaptersLib.model.block.BlockInfo
import net.craftoriya.adaptersLib.model.math.LocationIContainer
import net.craftoriya.adaptersLib.port.world.IWorldQueryPort
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Levelled
import org.bukkit.block.data.Waterlogged

class BukkitWorldQueryAdapter : IWorldQueryPort {

    private val fullCache = HashMap<Material, Boolean>()

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

    override fun info(at: LocationIContainer): BlockInfo? {
        val world = Bukkit.getWorld(at.world) ?: return null
        val v = at.vec3I
        if (!world.isChunkLoaded(v.x shr 4, v.z shr 4)) return null
        val block = world.getBlockAt(v.x, v.y, v.z)
        return BlockInfo(
            block.type.name,
            block.type.hardness,
            block.type.isSolid,
            block.isPassable,
            block.isLiquid,
            (block.blockData as? Waterlogged)?.isWaterlogged == true,
            isFull(block.type),
            block.type in HAZARDS,
            block.type.hasGravity(),
            scoopable(block)
        )
    }

    override fun isFullBlock(material: String): Boolean {
        val mat = Material.matchMaterial(material) ?: return false
        return isFull(mat)
    }

    override fun isBlock(material: String): Boolean = Material.matchMaterial(material)?.isBlock == true

    override fun canPlace(material: String): Boolean {
        val mat = Material.matchMaterial(material) ?: return false
        return isFull(mat) && !mat.hasGravity()
    }

    private fun scoopable(block: Block): Boolean = when (block.type) {
        Material.POWDER_SNOW -> true
        Material.WATER, Material.LAVA -> (block.blockData as? Levelled)?.level == 0
        else -> false
    }

    private fun isFull(mat: Material): Boolean = fullCache.getOrPut(mat) { mat.isBlock && cube(mat) }

    private fun cube(mat: Material): Boolean {
        val world = Bukkit.getWorlds().firstOrNull() ?: return mat.isOccluding
        return runCatching {
            val boxes = mat.createBlockData().getCollisionShape(Location(world, 0.0, 0.0, 0.0)).boundingBoxes
            val b = boxes.singleOrNull() ?: return@runCatching false
            b.widthX >= 0.999 && b.height >= 0.999 && b.widthZ >= 0.999
        }.getOrDefault(mat.isOccluding)
    }

    private companion object {
        val HAZARDS = setOf(
            Material.FIRE,
            Material.SOUL_FIRE,
            Material.LAVA,
            Material.MAGMA_BLOCK,
            Material.CAMPFIRE,
            Material.SOUL_CAMPFIRE,
            Material.CACTUS,
            Material.SWEET_BERRY_BUSH,
            Material.POWDER_SNOW,
            Material.WITHER_ROSE,
            Material.COBWEB
        )
    }
}