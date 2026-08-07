package net.craftoriya.adaptersLib.tools

import net.craftoriya.adaptersLib.containers.EntityContainer
import net.craftoriya.adaptersLib.containers.RecipeContainer
import org.spongepowered.configurate.objectmapping.ConfigSerializable

interface ITradeBookPort {
    fun applyTrades(entity: EntityContainer, trades: List<RecipeContainer.Trades>, mode: TradeApplyMode)
}

@ConfigSerializable
enum class TradeApplyMode { ADD, REMOVE }