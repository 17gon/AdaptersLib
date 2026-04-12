package net.craftoriya.adaptersLib.event.events

import net.craftoriya.adaptersLib.containers.PlayerContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent
//Simple event container, no function should be here
class PlayerJumpDomainEvent(
    val player: PlayerContainer,
    override var isCancelled: Boolean = false
): DomainEvent(), Cancellable