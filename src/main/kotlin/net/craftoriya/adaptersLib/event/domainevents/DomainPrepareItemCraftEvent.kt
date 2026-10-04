package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.model.CraftingGridContainer
import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.ItemContainer

class DomainPrepareItemCraftEvent(
    val inventoryGrid: CraftingGridContainer,
    val isRepair: Boolean,
    var result: ItemContainer? = null
): DomainEvent()