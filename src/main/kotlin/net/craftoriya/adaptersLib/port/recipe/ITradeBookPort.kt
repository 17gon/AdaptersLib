package net.craftoriya.adaptersLib.port.recipe

import net.craftoriya.adaptersLib.model.entity.EntityContainer
import net.craftoriya.adaptersLib.model.item.RecipeContainer
import net.craftoriya.adaptersLib.model.item.TradeApplyMode

interface ITradeBookPort {
    fun applyTrades(entity: EntityContainer, trades: List<RecipeContainer.Trades>, mode: TradeApplyMode)
}