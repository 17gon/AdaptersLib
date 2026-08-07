package net.craftoriya.adaptersLib.listeners

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.craftoriya.adaptersLib.containers.CraftingGridContainer
import net.craftoriya.adaptersLib.containers.EntityContainer
import net.craftoriya.adaptersLib.containers.InventoryTypeDomain
import net.craftoriya.adaptersLib.containers.ItemContainer
import net.craftoriya.adaptersLib.containers.PlayerContainer
import net.craftoriya.adaptersLib.containers.RecipeContainer
import net.craftoriya.adaptersLib.tools.Vec3D
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.events.DomainCraftingCompleteEvent
import net.craftoriya.adaptersLib.event.events.DomainFurnaceSmeltEvent
import net.craftoriya.adaptersLib.event.events.DomainFurnaceStartSmeltEvent
import net.craftoriya.adaptersLib.event.events.DomainPlayerJoinEvent
import net.craftoriya.adaptersLib.event.events.DomainPrepareItemCraftEvent
import net.craftoriya.adaptersLib.event.events.DomainPlayerJumpEvent
import net.craftoriya.adaptersLib.event.events.DomainVillagerInteractEvent
import net.craftoriya.adaptersLib.mappers.ItemStackMapper
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
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.inventory.FurnaceStartSmeltEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.CookingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class PaperEventListener(private val bus: DomainEventBus): Listener {
    private val itemMapper: ItemStackMapper = ItemStackMapper

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val pos = Vec3D(player.location.x, player.location.y, player.location.z)
        val playerContainer = PlayerContainer(player.uniqueId, player.name, pos, player.isOnGround)

        val domainEvent = DomainPlayerJoinEvent(playerContainer, event.joinMessage())

        bus.publish(domainEvent)
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player
        val pos = Vec3D(player.location.x, player.location.y, player.location.z)
        val playerContainer = PlayerContainer(player.uniqueId, player.name, pos, player.isOnGround)
        val domainEvent = DomainPlayerJumpEvent(playerContainer)

        bus.publish(domainEvent)
        if (domainEvent.isCancelled) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPrepareItemCraftEvent(event: PrepareItemCraftEvent) {
        val items = event.inventory.map { item ->
            if (item == null || item.type.isAir) return@map null
            else buildItemContainer(item)
        }

        val type: InventoryTypeDomain = when (event.inventory.type) {
            InventoryType.WORKBENCH -> InventoryTypeDomain.WORKBENCH
            InventoryType.CRAFTING -> InventoryTypeDomain.CRAFTING
            else -> return
        }
        val grid = CraftingGridContainer(type, items)
        val domainEvent = DomainPrepareItemCraftEvent(grid, event.isRepair)

        bus.publish(domainEvent)
        if (domainEvent.result != null) {
            event.inventory.result = itemMapper.toItemStack(domainEvent.result!!)
        } else {
            event.inventory.result = ItemStack(Material.AIR)
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onCraftItemEvent(event: CraftItemEvent) {
        val items = event.inventory.map { item ->
            if (item == null || item.type.isAir) return@map null
            else buildItemContainer(item)
        }
        val type: InventoryTypeDomain = when (event.inventory.type) {
            InventoryType.WORKBENCH -> InventoryTypeDomain.WORKBENCH
            InventoryType.CRAFTING -> InventoryTypeDomain.CRAFTING
            else -> return
        }
        val grid = CraftingGridContainer(type, items)
        val domainEvent = DomainCraftingCompleteEvent(grid)
        bus.publish(domainEvent)
        if (domainEvent.isCancelled) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onPrepareFurnaceSmeltEvent(event: FurnaceStartSmeltEvent) {
        val domainRecipe = buildDomainCooking(event.recipe, event.source, event.block) ?: return
        val domainEvent = DomainFurnaceStartSmeltEvent(event.totalCookTime, domainRecipe)
        bus.publish(domainEvent)
        if (domainEvent.isCancelled) {
//            (event.block as? Furnace)?.inventory?.result = ItemStack(Material.AIR)
            event.totalCookTime = 0
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onFurnaceSmeltEvent(event: FurnaceSmeltEvent) {
        val recipe = event.recipe ?: return
        val domainRecipe = buildDomainCooking(recipe, event.source, event.block) ?: return
        val domainEvent = DomainFurnaceSmeltEvent(domainRecipe)
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
        val player = event.player
        val pos = Vec3D(player.location.x, player.location.y, player.location.z)
        val playerContainer = PlayerContainer(player.uniqueId, player.name, pos, player.isOnGround)
        val entityContainer = EntityContainer(villager.uniqueId, "VILLAGER", villager.name)
        val profession = mapProfession(villager.profession)

        bus.publish(DomainVillagerInteractEvent(playerContainer, entityContainer, profession, villager.villagerLevel))
    }

    private fun mapProfession(p: Villager.Profession): RecipeContainer.TradeProfession = when (p) {
        Villager.Profession.ARMORER -> RecipeContainer.TradeProfession.ARMORER
        Villager.Profession.BUTCHER -> RecipeContainer.TradeProfession.BUTCHER
        Villager.Profession.CARTOGRAPHER -> RecipeContainer.TradeProfession.CARTOGRAPHER
        Villager.Profession.CLERIC -> RecipeContainer.TradeProfession.CLERIC
        Villager.Profession.FARMER -> RecipeContainer.TradeProfession.FARMER
        Villager.Profession.FISHERMAN -> RecipeContainer.TradeProfession.FISHERMAN
        Villager.Profession.FLETCHER -> RecipeContainer.TradeProfession.FLETCHER
        Villager.Profession.LEATHERWORKER -> RecipeContainer.TradeProfession.LEATHERWORKER
        Villager.Profession.LIBRARIAN -> RecipeContainer.TradeProfession.LIBRARIAN
        Villager.Profession.MASON -> RecipeContainer.TradeProfession.MASON
        Villager.Profession.SHEPHERD -> RecipeContainer.TradeProfession.SHEPHERD
        Villager.Profession.TOOLSMITH -> RecipeContainer.TradeProfession.TOOLSMITH
        Villager.Profession.WEAPONSMITH -> RecipeContainer.TradeProfession.WEAPONSMITH
        else -> RecipeContainer.TradeProfession.NONE
    }

    private fun buildItemContainer(item: ItemStack): ItemContainer {
        val data = mutableMapOf<String, String>()
        item.itemMeta?.let { meta ->
            val container = meta.persistentDataContainer
            container.keys.forEach { key ->
                val value: String? = when {
                    container.has(key, PersistentDataType.STRING) -> container.get(key, PersistentDataType.STRING)
                    container.has(key, PersistentDataType.INTEGER) -> container.get(key, PersistentDataType.INTEGER)?.toString()
                    container.has(key, PersistentDataType.DOUBLE) -> container.get(key, PersistentDataType.DOUBLE)?.toString()
                    container.has(key, PersistentDataType.LONG) -> container.get(key, PersistentDataType.LONG)?.toString()
                    container.has(key, PersistentDataType.BYTE) -> container.get(key, PersistentDataType.BYTE)?.toString()
                    else -> null
                }
                if (value != null) data[key.key] = value
            }
        }
        return ItemContainer(
            item.itemMeta?.displayName()?.toString() ?: "",
            item.type.toString(),
            item.amount,
            data
        )
    }

    private fun buildDomainCooking(recipe: CookingRecipe<*>, source: ItemStack, block: Block): RecipeContainer.Cooking? {
        val type = cookingTypeOf(block) ?: return null
        return RecipeContainer.Cooking(
            buildItemContainer(recipe.result),
            buildItemContainer(source),
            recipe.experience,
            recipe.cookingTime,
            type
        )
    }

    private fun cookingTypeOf(block: Block): RecipeContainer.CookingType? = when (val state = block.state) {
        is BlastFurnace -> RecipeContainer.CookingType.BLASTING;
        is Smoker -> RecipeContainer.CookingType.SMOKING;
        is Furnace -> RecipeContainer.CookingType.FURNACE;
        is Campfire -> RecipeContainer.CookingType.CAMPFIRE;
        else -> null
    }
}