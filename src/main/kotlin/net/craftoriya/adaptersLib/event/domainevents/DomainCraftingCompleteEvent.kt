package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.model.CraftingGridContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainCraftingCompleteEvent(
    val inventoryGrid: CraftingGridContainer,
    override var isCancelled: Boolean = false
): DomainEvent(), Cancellable;