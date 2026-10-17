package net.craftoriya.adaptersLib.model.block

import net.craftoriya.adaptersLib.model.item.ItemContainer

data class BreakResult(val drops: List<ItemContainer>, val toolBroken: Boolean)