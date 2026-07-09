package net.craftoriya.adaptersLib.tools

import net.craftoriya.adaptersLib.containers.ItemContainer
import net.craftoriya.adaptersLib.containers.RecipeContainer
import net.craftoriya.adaptersLib.containers.RecipeDto
import net.craftoriya.adaptersLib.containers.RecipesConfig
import net.craftoriya.adaptersLib.containers.TagsConfig

object RecipeExpander {
    fun expand(recipes: RecipesConfig, tags: TagsConfig): List<RecipeContainer> =
        recipes.recipes.map { expand(it, tags.tags) }

    private fun expand(dto: RecipeDto, tags: Map<String, List<String>>): RecipeContainer {
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
}
