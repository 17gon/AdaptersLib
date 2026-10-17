package net.craftoriya.adaptersLib.event.domainevents.inventory

import net.craftoriya.adaptersLib.model.item.InventoryContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.item.ItemContainer

class DomainInventoryMoveItemEvent(
    val source: InventoryContainer,
    val destination: InventoryContainer,
    val item: ItemContainer,
    val initiator: InventoryContainer,
    override var isCancelled: Boolean = false
): DomainEvent(), Cancellable