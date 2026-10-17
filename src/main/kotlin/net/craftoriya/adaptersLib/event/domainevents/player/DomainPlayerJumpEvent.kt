package net.craftoriya.adaptersLib.event.domainevents.player

import net.craftoriya.adaptersLib.model.entity.PlayerContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainPlayerJumpEvent(
    val player: PlayerContainer,
    override var isCancelled: Boolean = false
): DomainEvent(), Cancellable