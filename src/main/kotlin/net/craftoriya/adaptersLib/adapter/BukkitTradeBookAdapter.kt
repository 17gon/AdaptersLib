package net.craftoriya.adaptersLib.adapter

import net.craftoriya.adaptersLib.model.EntityContainer
import net.craftoriya.adaptersLib.model.RecipeContainer
import net.craftoriya.adaptersLib.port.ITradeBookPort
import net.craftoriya.adaptersLib.port.TradeApplyMode
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

class BukkitTradeBookAdapter : ITradeBookPort {

    override fun applyTrades(entity: EntityContainer, trades: List<RecipeContainer.Trades>, mode: TradeApplyMode) {
        val villager = Bukkit.getEntity(entity.id) as? Villager ?: return
        val built = trades.map { merchantRecipe(it) }
        villager.recipes = when (mode) {
            TradeApplyMode.ADD -> (villager.recipes + built).toMutableList()
            TradeApplyMode.REMOVE -> {
                val removed = built.map { it.result }
                villager.recipes.filterNot { existing -> removed.any { it.isSimilar(existing.result) } }.toMutableList()
            }
        }
    }

    private fun merchantRecipe(trade: RecipeContainer.Trades) = MerchantRecipe(
        ItemStack(Material.valueOf(trade.output.material), trade.output.count),
        trade.uses, trade.maxUses, trade.experienceReward,
        trade.villagerExperience, trade.priceMultiplier,
        trade.demand, trade.specialPrice, trade.ignoreDiscounts
    ).apply {
        ingredients = trade.ingredients.map { ItemStack(Material.valueOf(it.material), it.count) }
    }
}