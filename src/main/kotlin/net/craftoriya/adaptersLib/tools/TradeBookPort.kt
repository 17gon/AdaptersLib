package net.craftoriya.adaptersLib.tools

import net.craftoriya.adaptersLib.containers.EntityContainer
import net.craftoriya.adaptersLib.containers.RecipeContainer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

class TradeBookPort : ITradeBookPort {
    override fun applyTrades(entity: EntityContainer, trades: List<RecipeContainer.Trades>, mode: TradeApplyMode) {
        val villager = Bukkit.getEntity(entity.id) as? Villager ?: return
        val built = trades.map { buildTrade(it) }
        villager.recipes = when (mode) {
            TradeApplyMode.ADD -> (villager.recipes + built).toMutableList()
            TradeApplyMode.REMOVE -> {
                val outputsToRemove = built.map { it.result }
                villager.recipes.filterNot { existing -> outputsToRemove.any { it.isSimilar(existing.result) } }
                    .toMutableList()
            }
        }
    }

    private fun buildTrade(recipe: RecipeContainer.Trades): MerchantRecipe {
        val mr = MerchantRecipe(
            ItemStack(Material.valueOf(recipe.output.material), recipe.output.count),
            recipe.uses, recipe.maxUses, recipe.experienceReward,
            recipe.villagerExperience, recipe.priceMultiplier,
            recipe.demand, recipe.specialPrice, recipe.ignoreDiscounts
        )
        mr.ingredients = recipe.ingredients.map { ItemStack(Material.valueOf(it.material), it.count) }
        return mr
    }
}