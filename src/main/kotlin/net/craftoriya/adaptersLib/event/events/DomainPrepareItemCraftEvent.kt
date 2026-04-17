package net.craftoriya.adaptersLib.event.events

import net.craftoriya.adaptersLib.containers.CraftingGridContainer
import net.craftoriya.adaptersLib.containers.ItemContainer
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainPrepareItemCraftEvent(
    val inventoryGrid: CraftingGridContainer,
    val isRepair: Boolean,
    var result: ItemContainer? = null
): DomainEvent()