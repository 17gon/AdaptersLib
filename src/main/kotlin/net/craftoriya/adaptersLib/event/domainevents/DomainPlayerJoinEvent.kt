package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.model.PlayerContainer
import net.craftoriya.adaptersLib.event.DomainEvent
import net.kyori.adventure.text.Component

class DomainPlayerJoinEvent (
    val player: PlayerContainer,
    val chatMessage: Component?
): DomainEvent()