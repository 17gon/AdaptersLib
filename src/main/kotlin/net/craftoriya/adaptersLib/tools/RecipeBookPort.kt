package net.craftoriya.adaptersLib.tools

import net.craftoriya.adaptersLib.containers.ItemContainer
import net.craftoriya.adaptersLib.containers.PlayerContainer
import net.craftoriya.adaptersLib.containers.RecipeContainer

import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import kotlin.collections.chunked
import kotlin.collections.joinToString

class RecipeBookPort (
    private val plugin: Plugin
): IRecipeBookPort {

    override fun registerRecipe(key: String, recipe: RecipeContainer) {
        val nsKey = NamespacedKey(plugin, key)
        Bukkit.removeRecipe(nsKey)

        val outputStack = ItemStack(Material.valueOf(recipe.output.material), recipe.output.count)
        when (recipe) {
            is RecipeContainer.Shaped -> createShaped(nsKey, outputStack, recipe)
            is RecipeContainer.Shapeless -> createShapeless(nsKey, outputStack, recipe)
        }
    }

    override fun unregisterRecipe(key: String) {
        Bukkit.removeRecipe(NamespacedKey(plugin, key))
    }

    override fun discoverFor(player: PlayerContainer, key: String) {
        val bucketPlayer = Bukkit.getPlayer(player.id)?: return
        bucketPlayer.discoverRecipe(NamespacedKey(plugin, key))
    }



    public fun replaceRecipe(key: String, recipe: RecipeContainer) {
        unregisterRecipe(key)
        registerRecipe(key, recipe)
    }

    fun removeVanillaRecipesFor(recipeOutput: ItemContainer) {
        val material: Material = Material.valueOf(recipeOutput.material)
        val toRemove = mutableListOf<NamespacedKey>()
        val iter = Bukkit.recipeIterator()
        while (iter.hasNext()) {
            val r = iter.next()
            if (r is Keyed && r.result.type == material) {
                val key = (r as Keyed).key
                if (key.namespace == "minecraft") toRemove.add(key)
            }
        }
        toRemove.forEach { Bukkit.removeRecipe(it) }
    }



    private fun createShaped(nsKey: NamespacedKey, outputStack: ItemStack, recipe: RecipeContainer.Shaped) {
        val shaped = ShapedRecipe(nsKey, outputStack)

        val materials = recipe.pattern.filterNotNull().map { it.material }.distinct()
        val symbols = materials.mapIndexed { i, mat -> mat to "IJKLMNOPQ"[i] }.toMap()

        val rows = recipe.pattern.chunked(3).map { row ->
            row.joinToString("") { it?.let { symbols[it.material].toString() } ?: " " }
        }
        shaped.shape(*rows.toTypedArray())

        materials.forEach { mat ->
            shaped.setIngredient(symbols[mat]!!, RecipeChoice.MaterialChoice(Material.valueOf(mat)))
        }
        Bukkit.addRecipe(shaped)
    }

    private fun createShapeless(nsKey: NamespacedKey, outputStack: ItemStack, recipe: RecipeContainer.Shapeless) {
        val shapeless = ShapelessRecipe(nsKey, outputStack)
        recipe.ingredients.forEach {
            shapeless.addIngredient(RecipeChoice.MaterialChoice(Material.valueOf(it.material)))
        }
        Bukkit.addRecipe(shapeless)
    }
}