package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.ItemContainer
import net.craftoriya.adaptersLib.model.PlayerContainer
import net.craftoriya.adaptersLib.model.RecipeContainer

interface IRecipeBookPort {
    fun registerRecipe(key: String, recipe: RecipeContainer)
    fun unregisterRecipe(key: String)
    fun removeVanillaRecipesFor(output: ItemContainer)
    fun discoverFor(player: PlayerContainer, key: String)
}