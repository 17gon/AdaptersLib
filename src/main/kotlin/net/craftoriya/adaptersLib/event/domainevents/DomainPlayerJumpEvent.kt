package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.model.PlayerContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainPlayerJumpEvent(
    val player: PlayerContainer,
    override var isCancelled: Boolean = false
): DomainEvent(), Cancellable