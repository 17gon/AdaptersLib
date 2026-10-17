package net.craftoriya.adaptersLib.adapter.world

import net.craftoriya.adaptersLib.adapter.mapper.BlockMapper
import net.craftoriya.adaptersLib.adapter.mapper.ItemStackMapper.toItemContainer
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.domainevents.block.DomainBlockChangeEvent
import net.craftoriya.adaptersLib.event.domainevents.block.DomainBlockDropEvent
import net.craftoriya.adaptersLib.model.block.BlockChange
import net.craftoriya.adaptersLib.model.block.BreakResult
import net.craftoriya.adaptersLib.model.item.ItemContainer
import net.craftoriya.adaptersLib.model.item.ItemSource
import net.craftoriya.adaptersLib.model.math.LocationIContainer
import net.craftoriya.adaptersLib.port.world.IBlockBreakPort
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.block.Container
import org.bukkit.block.data.Levelled
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.potion.PotionEffectType
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

class BukkitBlockBreakAdapter(private val bus: DomainEventBus) : IBlockBreakPort {

    override fun breakTicks(breaker: UUID, at: LocationIContainer, tool: ItemSource): Int? {
        val entity: LivingEntity = living(breaker) ?: return null
        val block: Block = block(at) ?: return null
        val hardness = block.type.hardness
        if (hardness < 0f) return null
        if (hardness == 0f) return 1

        val stack = resolve(entity, tool)
        var speed = block.getDestroySpeed(stack, true)
        entity.getPotionEffect(PotionEffectType.HASTE)?.let { speed *= 1f + 0.2f * (it.amplifier + 1) }
        entity.getPotionEffect(PotionEffectType.MINING_FATIGUE)?.let { speed *= 0.3f.pow(min(it.amplifier + 1, 4)) }
        if (speed <= 0f) return Int.MAX_VALUE - 1

        val divisor = if (block.isPreferredTool(stack)) 30f else 100f
        val perTick = speed / hardness / divisor
        return if (perTick >= 1f) 1 else ceil(1f / perTick).toInt()
    }

    override fun breakBlock(breaker: UUID, at: LocationIContainer, tool: ItemSource): BreakResult {
        val entity: LivingEntity = living(breaker) ?: return BreakResult(emptyList(), false)
        val block: Block = block(at) ?: return BreakResult(emptyList(), false)
        if (block.type.isAir) return BreakResult(emptyList(), false)

        val stack = resolve(entity, tool)
        val container = BlockMapper.toContainer(block)

        val drops = ArrayList<ItemContainer>()
        block.getDrops(stack, entity).forEach { drops += toItemContainer(it) }
        val inv = ownInventory(block)
        inv?.contents?.forEach { if (it != null && !it.type.isAir) drops += toItemContainer(it) }
        inv?.clear()

        val event = DomainBlockDropEvent(container, breaker, drops)
        bus.publish(event)

        val data = block.blockData
        val center: Location = block.location.add(0.5, 0.5, 0.5)
        block.world.spawnParticle(Particle.BLOCK, center, 20, 0.25, 0.25, 0.25, data)
        block.world.playSound(center, data.soundGroup.breakSound, 1f, data.soundGroup.pitch)
        block.type = Material.AIR

        val broken: Boolean = wear(entity, tool, stack)
        bus.publish(DomainBlockChangeEvent(container, BlockChange.BREAK))
        return BreakResult(event.drops, broken)
    }

    override fun scoop(breaker: UUID, at: LocationIContainer, bucket: ItemSource): ItemContainer? {
        val entity: LivingEntity = living(breaker) ?: return null
        val block: Block = block(at) ?: return null
        val filled: Material = when (block.type) {
            Material.POWDER_SNOW -> Material.POWDER_SNOW_BUCKET
            Material.WATER -> if (isSource(block)) Material.WATER_BUCKET else return null
            Material.LAVA -> if (isSource(block)) Material.LAVA_BUCKET else return null
            else -> return null
        }
        val stack = resolve(entity, bucket)
        if (stack.type != Material.BUCKET || stack.amount < 1) return null

        val container = BlockMapper.toContainer(block)
        consumeOne(entity, bucket, stack)
        val center: Location = block.location.add(0.5, 0.5, 0.5)
        val sound = if (filled == Material.LAVA_BUCKET) Sound.ITEM_BUCKET_FILL_LAVA else Sound.ITEM_BUCKET_FILL
        block.world.playSound(center, sound, 1f, 1f)
        block.type = Material.AIR
        bus.publish(DomainBlockChangeEvent(container, BlockChange.BREAK))
        return toItemContainer(ItemStack(filled))
    }

    override fun place(at: LocationIContainer, material: String): Boolean {
        val block: Block = block(at) ?: return false
        val mat = Material.matchMaterial(material) ?: return false
        if (!mat.isBlock || mat.hasGravity()) return false
        val cur = block.type
        if (!(cur.isAir || block.isLiquid || cur == Material.POWDER_SNOW)) return false

        block.type = mat
        val data = block.blockData
        val center: Location = block.location.add(0.5, 0.5, 0.5)
        block.world.playSound(center, data.soundGroup.placeSound, 1f, data.soundGroup.pitch)
        bus.publish(DomainBlockChangeEvent(BlockMapper.toContainer(block), BlockChange.PLACE))
        return true
    }

    private fun isSource(block: Block): Boolean = (block.blockData as? Levelled)?.level == 0

    private fun consumeOne(entity: LivingEntity, src: ItemSource, stack: ItemStack) {
        if (stack.amount > 1) {
            stack.amount -= 1
            return
        }
        val c = src.container
        val en = src.entity
        when {
            c != null -> inventory(c)?.setItem(src.slot, null)
            en != null -> holderInventory(en)?.setItem(src.slot, null)
            else -> entity.equipment?.setItemInMainHand(null)
        }
    }

    private fun wear(entity: LivingEntity, src: ItemSource, stack: ItemStack): Boolean {
        if (stack.type.isAir) return false
        val meta = stack.itemMeta as? Damageable ?: return false
        val max = if (meta.hasMaxDamage()) meta.maxDamage else stack.type.maxDurability.toInt()
        if (max <= 0) return false

        val unbreaking = stack.getEnchantmentLevel(Enchantment.UNBREAKING)
        if (unbreaking > 0 && Random.nextInt(unbreaking + 1) != 0) return false

        val next = meta.damage + 1
        if (next >= max) {
            val c = src.container
            val en = src.entity
            when {
                c != null -> inventory(c)?.setItem(src.slot, null)
                en != null -> holderInventory(en)?.setItem(src.slot, null)
                else -> entity.equipment?.setItemInMainHand(null)
            }
            entity.world.playSound(entity.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
            return true
        }
        meta.damage = next
        stack.itemMeta = meta
        return false
    }

    private fun resolve(entity: LivingEntity, src: ItemSource): ItemStack {
        val c = src.container
        val en = src.entity
        val stack = when {
            c != null -> inventory(c)?.getItem(src.slot)
            en != null -> holderInventory(en)?.getItem(src.slot)
            else -> entity.equipment?.itemInMainHand
        }
        return stack ?: ItemStack(Material.AIR)
    }

    private fun holderInventory(id: UUID): Inventory? = (Bukkit.getEntity(id) as? InventoryHolder)?.inventory

    private fun living(id: UUID): LivingEntity? = Bukkit.getEntity(id) as? LivingEntity

    private fun block(at: LocationIContainer): Block? {
        val world = Bukkit.getWorld(at.world) ?: return null
        val vec = at.vec3I
        if (!world.isChunkLoaded(vec.x shr 4, vec.z shr 4)) return null
        return world.getBlockAt(vec.x, vec.y, vec.z)
    }

    private fun inventory(at: LocationIContainer): Inventory? =
        (block(at)?.getState(false) as? Container)?.inventory

    private fun ownInventory(b: Block): Inventory? = when (val s = b.getState(false)) {
        is Chest -> s.blockInventory
        is Container -> s.inventory
        else -> null
    }
}