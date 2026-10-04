package net.craftoriya.adaptersLib.model

import net.craftoriya.adaptersLib.port.TradeApplyMode

sealed interface RecipeContainer {
    val output: ItemContainer

    data class Shaped(
        override val output: ItemContainer,
        val pattern: List<ItemContainer?> // 9 slots, null = air
    ) : RecipeContainer

    data class Shapeless(
        override val output: ItemContainer,
        val ingredients: List<ItemContainer>
    ) : RecipeContainer

    data class Cooking(
        override val output: ItemContainer,
        val input: ItemContainer,
        val experience: Float,
        val cookingTick: Int,
        val type: CookingType = CookingType.FURNACE
    ) : RecipeContainer

    enum class CookingType { FURNACE, BLASTING, SMOKING, CAMPFIRE }

    data class Trades(
        override val output: ItemContainer,
        var maxUses: Int,
        var uses: Int,
        var demand: Int,
        var priceMultiplier: Float,
        var specialPrice: Int,
        var villagerExperience: Int,
        var experienceReward: Boolean,
        var ignoreDiscounts: Boolean,
        val ingredients: List<ItemContainer>,
        var level: Int,
        val mode: TradeApplyMode
    ): RecipeContainer

    enum class TradeProfession {
        ARMORER, BUTCHER, CARTOGRAPHER, CLERIC, FARMER, FISHERMAN, FLETCHER, LEATHERWORKER,
        LIBRARIAN, MASON, NITWIT, SHEPHERD, TOOLSMITH, WEAPONSMITH, NONE
    }
}
