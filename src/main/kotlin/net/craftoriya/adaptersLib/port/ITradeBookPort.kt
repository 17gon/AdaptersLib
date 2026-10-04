package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.EntityContainer
import net.craftoriya.adaptersLib.model.RecipeContainer

interface ITradeBookPort {
    fun applyTrades(entity: EntityContainer, trades: List<RecipeContainer.Trades>, mode: TradeApplyMode)
}