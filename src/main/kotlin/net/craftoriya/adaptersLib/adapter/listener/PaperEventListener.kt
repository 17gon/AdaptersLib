package net.craftoriya.adaptersLib.adapter.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.craftoriya.adaptersLib.adapter.mapper.BlockMapper
import net.craftoriya.adaptersLib.adapter.mapper.InventoryMapper
import net.craftoriya.adaptersLib.adapter.mapper.ItemStackMapper
import net.craftoriya.adaptersLib.adapter.mapper.ItemStackMapper.toItemContainer
import net.craftoriya.adaptersLib.adapter.mapper.PlayerMapper
import net.craftoriya.adaptersLib.adapter.mapper.ProfessionMapper
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.domainevents.DomainCraftingCompleteEvent
import net.craftoriya.adaptersLib.event.domainevents.DomainFurnaceSmeltEvent
import net.craftoriya.adaptersLib.event.domainevents.DomainFurnaceStartSmeltEvent
import net.craftoriya.adaptersLib.event.domainevents.DomainInventoryMoveItemEvent
import net.craftoriya.adaptersLib.event.domainevents.DomainPlayerInteractEvent
import net.craftoriya.adaptersLib.event.domainevents.DomainPlayerJoinEvent
import net.craftoriya.adaptersLib.event.domainevents.DomainPlayerJumpEvent
import net.craftoriya.adaptersLib.event.domainevents.DomainPrepareItemCraftEvent
import net.craftoriya.adaptersLib.event.domainevents.DomainVillagerInteractEvent
import net.craftoriya.adaptersLib.event.domainevents.InteractAction
import net.craftoriya.adaptersLib.model.EntityContainer
import net.craftoriya.adaptersLib.model.RecipeContainer
import org.bukkit.Material
import org.bukkit.block.BlastFurnace
import org.bukkit.block.Block
import org.bukkit.block.Campfire
import org.bukkit.block.Furnace
import org.bukkit.block.Smoker
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.inventory.FurnaceStartSmeltEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.CookingRecipe
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class PaperEventListener(private val bus: DomainEventBus) : Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        bus.publish(DomainPlayerJoinEvent(PlayerMapper.toContainer(event.player), event.joinMessage()))
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerJump(event: PlayerJumpEvent) {
        val domainEvent = DomainPlayerJumpEvent(PlayerMapper.toContainer(event.player))
        bus.publish(domainEvent)
        if (domainEvent.isCancelled) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPrepareItemCraft(event: PrepareItemCraftEvent) {
        val grid = InventoryMapper.craftingGrid(event.inventory) ?: return
        val domainEvent = DomainPrepareItemCraftEvent(grid, event.isRepair)
        bus.publish(domainEvent)
        event.inventory.result = domainEvent.result?.let { ItemStackMapper.toItemStack(it) } ?: ItemStack(Material.AIR)
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onCraftItem(event: CraftItemEvent) {
        val grid = InventoryMapper.craftingGrid(event.inventory) ?: return
        val domainEvent = DomainCraftingCompleteEvent(grid)
        bus.publish(domainEvent)
        if (domainEvent.isCancelled) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onFurnaceStartSmelt(event: FurnaceStartSmeltEvent) {
        val recipe = cookingRecipe(event.recipe, event.source, event.block) ?: return
        val domainEvent = DomainFurnaceStartSmeltEvent(event.totalCookTime, recipe)
        bus.publish(domainEvent)
        if (domainEvent.isCancelled) event.totalCookTime = 0
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onFurnaceSmelt(event: FurnaceSmeltEvent) {
        val bukkitRecipe = event.recipe ?: return
        val recipe = cookingRecipe(bukkitRecipe, event.source, event.block) ?: return
        val domainEvent = DomainFurnaceSmeltEvent(recipe)
        bus.publish(domainEvent)
        if (domainEvent.isCancelled) {
            event.isCancelled = true
            event.result = ItemStack(Material.AIR)
        } else {
            (event.block.state as Furnace).inventory.smelting?.amount -= domainEvent.extraToConsume
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val villager = event.rightClicked as? Villager ?: return
        bus.publish(
            DomainVillagerInteractEvent(
                PlayerMapper.toContainer(event.player),
                EntityContainer(villager.uniqueId, "VILLAGER", villager.name),
                ProfessionMapper.map(villager.profession),
                villager.villagerLevel
            )
        )
    }

    @EventHandler
    fun onInventoryMoveItem(event: InventoryMoveItemEvent) {
        val domainEvent = DomainInventoryMoveItemEvent(
            InventoryMapper.toContainer(event.source),
            InventoryMapper.toContainer(event.destination),
            toItemContainer(event.item),
            InventoryMapper.toContainer(event.initiator)
        )
        bus.publish(domainEvent)
        if (domainEvent.isCancelled) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val action = when (event.action) {
            Action.LEFT_CLICK_AIR -> InteractAction.LEFT_AIR
            Action.LEFT_CLICK_BLOCK -> InteractAction.LEFT_BLOCK
            Action.RIGHT_CLICK_AIR -> InteractAction.RIGHT_AIR
            Action.RIGHT_CLICK_BLOCK -> InteractAction.RIGHT_BLOCK
            Action.PHYSICAL -> InteractAction.PHYSICAL
        }
        val player = event.player
        val domainEvent = DomainPlayerInteractEvent(
            PlayerMapper.toContainer(player),
            player.world.name,
            action,
            event.hand != EquipmentSlot.OFF_HAND,
            player.isSneaking,
            event.item?.takeUnless { it.type.isAir }?.let { toItemContainer(it) },
            event.clickedBlock?.let { BlockMapper.toContainer(it) }
        )
        bus.publish(domainEvent)
        if (domainEvent.isCancelled) event.isCancelled = true
    }

    private fun cookingRecipe(recipe: CookingRecipe<*>, source: ItemStack, block: Block): RecipeContainer.Cooking? {
        val type = when (block.state) {
            is BlastFurnace -> RecipeContainer.CookingType.BLASTING
            is Smoker -> RecipeContainer.CookingType.SMOKING
            is Furnace -> RecipeContainer.CookingType.FURNACE
            is Campfire -> RecipeContainer.CookingType.CAMPFIRE
            else -> return null
        }
        return RecipeContainer.Cooking(
            toItemContainer(recipe.result),
            toItemContainer(source),
            recipe.experience,
            recipe.cookingTime,
            type
        )
    }
}