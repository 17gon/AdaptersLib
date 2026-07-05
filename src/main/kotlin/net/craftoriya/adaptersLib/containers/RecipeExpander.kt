package net.craftoriya.adaptersLib.containers

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
                    tags[ref.removePrefix("#")]?.firstOrNull() ?: return@forEachIndexed
                else ref
                pattern[row * 3 + col] = ItemContainer(material, material, 1, emptyMap())
            }
        }
        return RecipeContainer.Shaped(
            ItemContainer(dto.output, dto.output, dto.count, emptyMap()),
            pattern
        )
    }
}
