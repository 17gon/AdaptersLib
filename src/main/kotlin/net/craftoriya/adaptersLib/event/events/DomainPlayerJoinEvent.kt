package net.craftoriya.adaptersLib.event.events

import net.craftoriya.adaptersLib.containers.PlayerContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent
import net.kyori.adventure.text.Component

class DomainPlayerJoinEvent (
    val player: PlayerContainer,
    val chatMessage: Component?
): DomainEvent()