package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.model.InventoryContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.ItemContainer

class DomainInventoryMoveItemEvent(
    val source: InventoryContainer,
    val destination: InventoryContainer,
    val item: ItemContainer,
    val initiator: InventoryContainer,
    override var isCancelled: Boolean = false
): DomainEvent(), Cancellable