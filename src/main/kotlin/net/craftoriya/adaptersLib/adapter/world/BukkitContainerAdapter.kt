package net.craftoriya.adaptersLib.adapter.world

import net.craftoriya.adaptersLib.adapter.mapper.ItemStackMapper.toItemContainer
import net.craftoriya.adaptersLib.adapter.mapper.ItemStackMapper.toItemStack
import net.craftoriya.adaptersLib.model.item.ItemContainer
import net.craftoriya.adaptersLib.model.math.LocationIContainer
import net.craftoriya.adaptersLib.model.item.SlotItem
import net.craftoriya.adaptersLib.port.world.IContainerPort
import org.bukkit.Bukkit
import org.bukkit.block.Container
import org.bukkit.inventory.Inventory

class BukkitContainerAdapter : IContainerPort {

    override fun contents(at: LocationIContainer): List<ItemContainer> {
        val inv = inventory(at) ?: return emptyList()
        val sums = LinkedHashMap<String, Int>()
        for (s in inv.contents) {
            if (s == null || s.type.isAir) continue
            sums.merge(s.type.toString(), s.amount, Int::plus)
        }
        return sums.map { ItemContainer(material = it.key, count = it.value) }
    }

    override fun slots(at: LocationIContainer): List<SlotItem> {
        val inv = inventory(at) ?: return emptyList()
        val out = ArrayList<SlotItem>()
        for (i in 0 until inv.size) {
            val s = inv.getItem(i) ?: continue
            if (s.type.isAir) continue
            out += SlotItem(i, toItemContainer(s))
        }
        return out
    }

    override fun take(at: LocationIContainer, filter: (ItemContainer) -> Boolean, max: Int): List<ItemContainer> {
        val inv = inventory(at) ?: return emptyList()
        val out: MutableList<ItemContainer> = mutableListOf()
        var left = max
        for (i in 0 until inv.size) {
            if (left <= 0) break
            val stack = inv.getItem(i) ?: continue
            if (stack.type.isAir) continue
            val c = toItemContainer(stack)
            if (!filter(c)) continue
            val n = minOf(stack.amount, left)
            out += c.copy(count = n)
            left -= n
            if (n == stack.amount) inv.setItem(i, null)
            else inv.setItem(i, stack.clone().also { it.amount = stack.amount - n })
        }
        return out
    }

    override fun put(at: LocationIContainer, items: List<ItemContainer>): List<ItemContainer> {
        val inv = inventory(at) ?: return items
        val leftovers = mutableListOf<ItemContainer>()
        for (item in items) {
            inv.addItem(toItemStack(item)).values.forEach { leftovers += toItemContainer(it) }
        }
        return leftovers
    }

    private fun inventory(at: LocationIContainer): Inventory? {
        val world = Bukkit.getWorld(at.world) ?: return null
        val vec = at.vec3I
        if (!world.isChunkLoaded(vec.x shr 4, vec.z shr 4)) return null
        return (world.getBlockAt(vec.x, vec.y, vec.z).getState(false) as? Container)?.inventory
    }
}
