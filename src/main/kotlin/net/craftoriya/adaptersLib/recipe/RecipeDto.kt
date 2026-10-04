package net.craftoriya.adaptersLib.config

import net.craftoriya.adaptersLib.model.ItemContainer
import net.craftoriya.adaptersLib.model.RecipeContainer
import net.craftoriya.adaptersLib.port.TradeApplyMode
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class RecipeShapedDto(
    val output: String = "",
    val count: Int = 1,
    val shape: List<String> = emptyList(),
    val key: Map<String, String> = emptyMap()
)
@ConfigSerializable
data class RecipeShapelessDto(
    val output: String = "",
    val count: Int = 1,
    val ingredients: List<String> = emptyList()
)

@ConfigSerializable
data class RecipeSmeltingDto(
    val input: String = "",
    val output: String = "",
    val count: Int = 1,
    val cookingTime: Int = 200,
    val experience: Float = 0f,
    val types: List<RecipeContainer.CookingType> = emptyList(),
    val intake: Int = 1
)

@ConfigSerializable
data class RecipeTradesDto(
    val output: ItemContainer = ItemContainer(),
    val maxUses: Int = 1,
    val uses: Int = 1,
    val demand: Int = 1,
    val priceMultiplier: Float = 0F,
    val specialPrice: Int = 1,
    val villagerExperience: Int = 1,
    val experienceReward: Boolean = false,
    val ignoreDiscounts: Boolean = false,
    val ingredients: List<ItemContainer> = emptyList(),
    val professions: List<RecipeContainer.TradeProfession> = emptyList(),
    val level: Int = 1,
    val mode: TradeApplyMode = TradeApplyMode.ADD,
)