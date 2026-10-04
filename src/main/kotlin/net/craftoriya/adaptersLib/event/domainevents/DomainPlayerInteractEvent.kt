package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.model.PlayerContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.BlockContainer
import net.craftoriya.adaptersLib.model.ItemContainer

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

enum class InteractAction { LEFT_AIR, LEFT_BLOCK, RIGHT_AIR, RIGHT_BLOCK, PHYSICAL }
