package net.craftoriya.adaptersLib.adapter

import net.craftoriya.adaptersLib.adapter.mapper.ItemStackMapper.toItemContainer
import net.craftoriya.adaptersLib.model.ItemContainer
import net.craftoriya.adaptersLib.model.ItemSource
import net.craftoriya.adaptersLib.model.LocationIContainer
import net.craftoriya.adaptersLib.model.SlotItem
import net.craftoriya.adaptersLib.model.Vec3D
import net.craftoriya.adaptersLib.port.IEntityPort
import org.bukkit.Bukkit
import org.bukkit.block.Container
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import java.util.UUID

class BukkitEntityAdapter : IEntityPort {

    override fun position(id: UUID): Vec3D? =
        Bukkit.getEntity(id)?.location?.let { Vec3D(it.x, it.y, it.z) }

    override fun mainHand(id: UUID): ItemContainer? {
        val item = living(id)?.equipment?.itemInMainHand ?: return null
        return if (item.type.isAir) null else toItemContainer(item)
    }

    override fun inventory(id: UUID): List<SlotItem> {
        val inv = holder(id) ?: return emptyList()
        val out = ArrayList<SlotItem>()
        for (i in 0 until inv.size) {
            val s = inv.getItem(i) ?: continue
            if (s.type.isAir) continue
            out += SlotItem(i, toItemContainer(s))
        }
        return out
    }

    override fun pull(id: UUID, from: LocationIContainer, slot: Int): Int? {
        val inv = holder(id) ?: return null
        val src = blockInventory(from) ?: return null
        val stack = src.getItem(slot) ?: return null
        if (stack.type.isAir) return null
        val free = inv.firstEmpty()
        if (free < 0) return null

        inv.setItem(free, stack.clone().also { it.amount = 1 })
        if (stack.amount > 1) stack.amount -= 1 else src.setItem(slot, null)
        return free
    }

    override fun push(id: UUID, slot: Int, to: LocationIContainer): Boolean {
        val inv = holder(id) ?: return false
        val dst = blockInventory(to) ?: return false
        val item = inv.getItem(slot) ?: return true
        if (dst.addItem(item.clone()).isNotEmpty()) return false
        inv.setItem(slot, null)
        return true
    }

    override fun display(id: UUID, source: ItemSource?) {
        val eq = living(id)?.equipment ?: return
        eq.itemInMainHandDropChance = 0f
        if (source == null) {
            eq.setItemInMainHand(null)
            return
        }
        val c = source.container
        val en = source.entity
        val item = when {
            c != null -> blockInventory(c)?.getItem(source.slot)
            en != null -> holder(en)?.getItem(source.slot)
            else -> return
        }
        eq.setItemInMainHand(item?.clone()?.also { it.amount = 1 })
    }

    private fun living(id: UUID): LivingEntity? = Bukkit.getEntity(id) as? LivingEntity

    private fun holder(id: UUID): Inventory? = (Bukkit.getEntity(id) as? InventoryHolder)?.inventory

    private fun blockInventory(at: LocationIContainer): Inventory? {
        val world = Bukkit.getWorld(at.world) ?: return null
        val vec = at.vec3I
        if (!world.isChunkLoaded(vec.x shr 4, vec.z shr 4)) return null
        return (world.getBlockAt(vec.x, vec.y, vec.z).getState(false) as? Container)?.inventory
    }
}