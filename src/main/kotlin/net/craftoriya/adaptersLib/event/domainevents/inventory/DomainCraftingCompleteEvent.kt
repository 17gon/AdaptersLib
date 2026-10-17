package net.craftoriya.adaptersLib.event.domainevents.inventory

import net.craftoriya.adaptersLib.model.item.CraftingGridContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainCraftingCompleteEvent(
    val inventoryGrid: CraftingGridContainer,
    override var isCancelled: Boolean = false
): DomainEvent(), Cancellable