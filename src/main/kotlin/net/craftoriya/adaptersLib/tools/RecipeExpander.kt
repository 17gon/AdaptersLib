package net.craftoriya.adaptersLib.tools

import net.craftoriya.adaptersLib.containers.ItemContainer
import net.craftoriya.adaptersLib.containers.RecipeContainer
import net.craftoriya.adaptersLib.containers.RecipeShapedDto
import net.craftoriya.adaptersLib.containers.RecipeShapelessDto
import net.craftoriya.adaptersLib.containers.RecipeSmeltingDto
import net.craftoriya.adaptersLib.containers.RecipeTradesDto
import net.craftoriya.adaptersLib.containers.RecipesConfig
import net.craftoriya.adaptersLib.containers.TagsConfig
import kotlin.collections.getOrPut

object RecipeExpander {
    fun expand(recipesConfig: RecipesConfig, tagsConfig: TagsConfig): List<RecipeContainer> =
        recipesConfig.shaped.map { expandShaped(it, tagsConfig.tags) } +
        recipesConfig.shapeless.map { expandShapeless(it, tagsConfig.tags) } +
        recipesConfig.smelting.flatMap {
            expandSmelting(it, tagsConfig.tags) }

    fun expandTrades(recipesConfig: RecipesConfig): MutableMap<RecipeContainer.TradeProfession, MutableMap<Int, MutableList<RecipeContainer.Trades>>> {
        val masterMap = mutableMapOf<RecipeContainer.TradeProfession, MutableMap<Int, MutableList<RecipeContainer.Trades>>>()
        for (dto in recipesConfig.trades) {
            val expandedDtoMap = expandTrades(dto)
            for ((profession, tradeContainer: RecipeContainer.Trades) in expandedDtoMap) {
                val levelsList: MutableMap<Int, MutableList<RecipeContainer.Trades>> = masterMap.getOrPut(profession) { mutableMapOf() }
                levelsList.getOrPut(tradeContainer.level) {mutableListOf()}.add(tradeContainer)
            }
        }
        return masterMap
    }

    private fun resolve(ref: String, tags: Map<String, List<String>>): String? =
        if (ref.startsWith("#")) tags[ref.removePrefix("#")]?.firstOrNull()?.uppercase()
        else ref.uppercase()

    private fun expandShaped(dto: RecipeShapedDto, tags: Map<String, List<String>>): RecipeContainer {
        val pattern = MutableList<ItemContainer?>(9) { null }
        dto.shape.forEachIndexed { row, line ->
            line.forEachIndexed { col, char ->
                if (char == ' ') return@forEachIndexed
                val ref = dto.key[char.toString()] ?: return@forEachIndexed
                val material = if (ref.startsWith("#"))
                    tags[ref.removePrefix("#")]?.firstOrNull()?.uppercase() ?: return@forEachIndexed
                else ref.uppercase()
                pattern[row * 3 + col] = ItemContainer("", material, 1, emptyMap())
            }
        }
        return RecipeContainer.Shaped(
            ItemContainer("", dto.output, dto.count, emptyMap()),
            pattern
        )
    }

    private fun expandShapeless(dto: RecipeShapelessDto, tags: Map<String, List<String>>): RecipeContainer {
        val ingredients = dto.ingredients.mapNotNull { ref ->
            resolve(ref, tags)?.let { ItemContainer("", it, 1, emptyMap()) }
        }
        return RecipeContainer.Shapeless(ItemContainer("", dto.output, dto.count, emptyMap()), ingredients)
    }

    private fun expandSmelting(dto: RecipeSmeltingDto, tags: Map<String, List<String>>): List<RecipeContainer> {
        val material = resolve(dto.input, tags) ?: dto.input.uppercase()
        return dto.types.map {
            RecipeContainer.Cooking(
                ItemContainer("", dto.output, dto.count, emptyMap()),
                ItemContainer("", material, dto.intake, emptyMap()),
                dto.experience,
                dto.cookingTime,
                it
            )
        }
    }

    private fun expandTrades(dto: RecipeTradesDto): Map<RecipeContainer.TradeProfession, RecipeContainer.Trades> {
        return dto.professions.associateBy(
            { it },
            {
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
        )
    }


}
