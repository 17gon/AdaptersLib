package net.craftoriya.adaptersLib.event.domainevents.inventory

import net.craftoriya.adaptersLib.model.item.CraftingGridContainer
import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.item.ItemContainer

class DomainPrepareItemCraftEvent(
    val inventoryGrid: CraftingGridContainer,
    val isRepair: Boolean,
    var result: ItemContainer? = null
): DomainEvent()