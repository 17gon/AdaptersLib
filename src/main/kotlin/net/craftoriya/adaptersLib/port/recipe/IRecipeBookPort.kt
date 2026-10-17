package net.craftoriya.adaptersLib.port.recipe

import net.craftoriya.adaptersLib.model.item.ItemContainer
import net.craftoriya.adaptersLib.model.entity.PlayerContainer
import net.craftoriya.adaptersLib.model.item.RecipeContainer

interface IRecipeBookPort {
    fun registerRecipe(key: String, recipe: RecipeContainer)
    fun unregisterRecipe(key: String)
    fun removeVanillaRecipesFor(output: ItemContainer)
    fun discoverFor(player: PlayerContainer, key: String)
}