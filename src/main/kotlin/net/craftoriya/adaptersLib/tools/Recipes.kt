package net.craftoriya.adaptersLib.tools

import net.craftoriya.adaptersLib.containers.CraftingGridContainer
import net.craftoriya.adaptersLib.containers.InventoryTypeDomain
import net.craftoriya.adaptersLib.containers.ItemContainer

data class ShapedRecipe(
    val id: String,
    val width: Int,
    val height: Int,
    val pattern: List<ItemMatcher?>,
    val result: ItemContainer
)
data class ShapelessRecipe(
    val id: String,
    val ingredients: List<ItemMatcher>,
    val result: ItemContainer
)
data class ItemMatcher(
    val material: String,
    val requiredProperties: Map<String, String> = mapOf()
)
fun ShapedRecipe.matches(grid: CraftingGridContainer): Boolean {
    val (gridHeight, gridWidth) = when (grid.type) {
        InventoryTypeDomain.WORKBENCH -> 3 to 3
        InventoryTypeDomain.CRAFTING -> 2 to 2
        else -> 3 to 3
    }
    for (offsetY in 0..gridHeight - height) {
        for (offsetX in 0..gridWidth - width) {
            if (matchesAt(grid, offsetX, offsetY)) {
                return true
            }
        }
    }
    return false
}
fun ShapedRecipe.matchesAt(grid: CraftingGridContainer, offsetX: Int, offsetY: Int): Boolean {
    val (gridHeight, gridWidth) = when (grid.type) {
        InventoryTypeDomain.WORKBENCH -> 3 to 3
        InventoryTypeDomain.CRAFTING -> 2 to 2
        else -> 3 to 3
    }

    for (y in 0 until gridHeight) {
        for (x in 0 until gridWidth) {
            val inPattern = (x in offsetX until (offsetX + width)) && (y in offsetY until (offsetY + height))
            val gridIndex = y * gridWidth + x
            val actual = grid.items[gridIndex]

            if (inPattern) {
                val patternX = x - offsetX
                val patternY = y - offsetY
                val patternIndex = patternY * width + patternX
                val expected = pattern[patternIndex]

                if (!match(expected, actual)) return false
            } else {
                if (actual != null) return false
            }
        }
    }
    return true
}
fun match(expected: ItemMatcher?, actual: ItemContainer?): Boolean {
    return when {
        expected == null && actual == null -> true
        expected == null || actual == null -> false
        else -> actual.material == expected.material
            //&& expected.requiredProperties.all { (k, v) -> actual.properties[k] == v }
            //Uncomment in case we need to be specific with item data
    }
}