package net.craftoriya.adaptersLib.tools

import net.craftoriya.adaptersLib.containers.PlayerContainer
import net.craftoriya.adaptersLib.containers.RecipeContainer

interface IRecipeBookPort {
    fun registerRecipe(key: String, recipe: RecipeContainer)
    fun unregisterRecipe(key: String)
    fun discoverFor(player: PlayerContainer, key: String)
}