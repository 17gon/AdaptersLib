package net.craftoriya.adaptersLib.adapter

import net.craftoriya.adaptersLib.model.ItemContainer
import net.craftoriya.adaptersLib.model.PlayerContainer
import net.craftoriya.adaptersLib.model.RecipeContainer
import net.craftoriya.adaptersLib.port.IRecipeBookPort
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.BlastingRecipe
import org.bukkit.inventory.CampfireRecipe
import org.bukkit.inventory.CookingRecipe
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.SmokingRecipe
import org.bukkit.plugin.Plugin

class BukkitRecipeBookAdapter(private val plugin: Plugin) : IRecipeBookPort {

    override fun registerRecipe(key: String, recipe: RecipeContainer) {
        val nsKey = NamespacedKey(plugin, keyOf(key, recipe))
        Bukkit.removeRecipe(nsKey)
        val result = stackOf(recipe.output)
        when (recipe) {
            is RecipeContainer.Shaped -> Bukkit.addRecipe(shaped(nsKey, result, recipe))
            is RecipeContainer.Shapeless -> Bukkit.addRecipe(shapeless(nsKey, result, recipe))
            is RecipeContainer.Cooking -> {
                removeVanillaRecipesFor(recipe.output)
                Bukkit.addRecipe(cooking(nsKey, result, recipe))
            }
            is RecipeContainer.Trades -> {}
        }
    }

    override fun unregisterRecipe(key: String) {
        Bukkit.removeRecipe(NamespacedKey(plugin, key))
    }

    override fun removeVanillaRecipesFor(output: ItemContainer) {
        val material = Material.valueOf(output.material)
        val vanilla = Bukkit.recipeIterator().asSequence()
            .filter { it.result.type == material }
            .filterIsInstance<Keyed>()
            .map { it.key }
            .filter { it.namespace == "minecraft" }
            .toList()
        vanilla.forEach { Bukkit.removeRecipe(it) }
    }

    override fun discoverFor(player: PlayerContainer, key: String) {
        Bukkit.getPlayer(player.id)?.discoverRecipe(NamespacedKey(plugin, key))
    }

    private fun keyOf(key: String, recipe: RecipeContainer) =
        if (recipe is RecipeContainer.Cooking) "${key}_${recipe.type}" else key

    private fun stackOf(item: ItemContainer) = ItemStack(Material.valueOf(item.material), item.count)

    private fun choiceOf(item: ItemContainer) = RecipeChoice.MaterialChoice(Material.valueOf(item.material))

    private fun shaped(key: NamespacedKey, result: ItemStack, recipe: RecipeContainer.Shaped): ShapedRecipe {
        val materials = recipe.pattern.filterNotNull().map { it.material }.distinct()
        val symbols = materials.withIndex().associate { (i, material) -> material to SYMBOLS[i] }
        val rows = recipe.pattern.chunked(3).map { row ->
            row.joinToString("") { item -> item?.let { symbols.getValue(it.material).toString() } ?: " " }
        }
        return ShapedRecipe(key, result).apply {
            shape(*rows.toTypedArray())
            symbols.forEach { (material, symbol) ->
                setIngredient(symbol, RecipeChoice.MaterialChoice(Material.valueOf(material)))
            }
        }
    }

    private fun shapeless(key: NamespacedKey, result: ItemStack, recipe: RecipeContainer.Shapeless) =
        ShapelessRecipe(key, result).apply {
            recipe.ingredients.forEach { addIngredient(choiceOf(it)) }
        }

    private fun cooking(key: NamespacedKey, result: ItemStack, recipe: RecipeContainer.Cooking): CookingRecipe<*> {
        val input = choiceOf(recipe.input)
        val xp = recipe.experience
        val time = recipe.cookingTick
        return when (recipe.type) {
            RecipeContainer.CookingType.FURNACE -> FurnaceRecipe(key, result, input, xp, time)
            RecipeContainer.CookingType.BLASTING -> BlastingRecipe(key, result, input, xp, time)
            RecipeContainer.CookingType.SMOKING -> SmokingRecipe(key, result, input, xp, time)
            RecipeContainer.CookingType.CAMPFIRE -> CampfireRecipe(key, result, input, xp, time)
        }
    }

    private companion object {
        const val SYMBOLS = "IJKLMNOPQ"
    }
}