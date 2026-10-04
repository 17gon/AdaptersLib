package net.craftoriya.adaptersLib.recipe

import net.craftoriya.adaptersLib.config.RecipeShapedDto
import net.craftoriya.adaptersLib.config.RecipeShapelessDto
import net.craftoriya.adaptersLib.config.RecipeSmeltingDto
import net.craftoriya.adaptersLib.config.RecipeTradesDto
import net.craftoriya.adaptersLib.config.RecipesConfig
import net.craftoriya.adaptersLib.config.TagsConfig
import net.craftoriya.adaptersLib.model.ItemContainer
import net.craftoriya.adaptersLib.model.RecipeContainer

typealias TradeTable = MutableMap<RecipeContainer.TradeProfession, MutableMap<Int, MutableList<RecipeContainer.Trades>>>

object RecipeExpander {

    fun expand(recipes: RecipesConfig, tags: TagsConfig): List<RecipeContainer> =
        recipes.shaped.map { expandShaped(it, tags.tags) } +
            recipes.shapeless.map { expandShapeless(it, tags.tags) } +
            recipes.smelting.flatMap { expandSmelting(it, tags.tags) }

    fun expandTrades(recipes: RecipesConfig): TradeTable {
        val table: TradeTable = mutableMapOf()
        for (dto in recipes.trades) {
            for ((profession, trade) in expandTrade(dto)) {
                table.getOrPut(profession) { mutableMapOf() }
                    .getOrPut(trade.level) { mutableListOf() }
                    .add(trade)
            }
        }
        return table
    }

    private fun resolve(ref: String, tags: Map<String, List<String>>): String? =
        if (ref.startsWith("#")) tags[ref.removePrefix("#")]?.firstOrNull()?.uppercase()
        else ref.uppercase()

    private fun item(material: String, count: Int) = ItemContainer("", material, count, emptyMap())

    private fun expandShaped(dto: RecipeShapedDto, tags: Map<String, List<String>>): RecipeContainer {
        val pattern = MutableList<ItemContainer?>(9) { null }
        dto.shape.forEachIndexed { row, line ->
            line.forEachIndexed { col, char ->
                if (char == ' ') return@forEachIndexed
                val ref = dto.key[char.toString()] ?: return@forEachIndexed
                val material = resolve(ref, tags) ?: return@forEachIndexed
                pattern[row * 3 + col] = item(material, 1)
            }
        }
        return RecipeContainer.Shaped(item(dto.output, dto.count), pattern)
    }

    private fun expandShapeless(dto: RecipeShapelessDto, tags: Map<String, List<String>>): RecipeContainer {
        val ingredients = dto.ingredients.mapNotNull { ref -> resolve(ref, tags)?.let { item(it, 1) } }
        return RecipeContainer.Shapeless(item(dto.output, dto.count), ingredients)
    }

    private fun expandSmelting(dto: RecipeSmeltingDto, tags: Map<String, List<String>>): List<RecipeContainer> {
        val material = resolve(dto.input, tags) ?: dto.input.uppercase()
        return dto.types.map {
            RecipeContainer.Cooking(
                item(dto.output, dto.count),
                item(material, dto.intake),
                dto.experience,
                dto.cookingTime,
                it
            )
        }
    }

    private fun expandTrade(dto: RecipeTradesDto): Map<RecipeContainer.TradeProfession, RecipeContainer.Trades> =
        dto.professions.associateWith {
            RecipeContainer.Trades(
                dto.output,
                dto.maxUses,
                dto.uses,
                dto.demand,
                dto.priceMultiplier,
                dto.specialPrice,
                dto.villagerExperience,
                dto.experienceReward,
                dto.ignoreDiscounts,
                dto.ingredients,
                dto.level,
                dto.mode
            )
        }
}