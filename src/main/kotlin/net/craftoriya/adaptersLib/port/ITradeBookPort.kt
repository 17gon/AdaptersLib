package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.EntityContainer
import net.craftoriya.adaptersLib.model.RecipeContainer
import net.craftoriya.adaptersLib.model.TradeApplyMode

interface ITradeBookPort {
    fun applyTrades(entity: EntityContainer, trades: List<RecipeContainer.Trades>, mode: TradeApplyMode)
}