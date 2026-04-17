package net.craftoriya.adaptersLib.event.events

import net.craftoriya.adaptersLib.containers.CraftingGridContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainCraftingCompleteEvent(
    val inventoryGrid: CraftingGridContainer,
    override var isCancelled: Boolean = false
): DomainEvent(), Cancellable;