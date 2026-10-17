package net.craftoriya.adaptersLib.event.domainevents.player

import net.craftoriya.adaptersLib.model.entity.PlayerContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.block.BlockContainer
import net.craftoriya.adaptersLib.model.entity.InteractAction
import net.craftoriya.adaptersLib.model.item.ItemContainer

class DomainPlayerInteractEvent(
    val player: PlayerContainer,
    val world: String,
    val action: InteractAction,
    val mainHand: Boolean,
    val sneaking: Boolean,
    val item: ItemContainer?,
    val block: BlockContainer?
) : DomainEvent(), Cancellable {
    override var isCancelled: Boolean = false
}
