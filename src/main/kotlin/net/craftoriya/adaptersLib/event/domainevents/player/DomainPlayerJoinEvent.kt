package net.craftoriya.adaptersLib.event.domainevents.player

import net.craftoriya.adaptersLib.model.entity.PlayerContainer
import net.craftoriya.adaptersLib.event.DomainEvent
import net.kyori.adventure.text.Component

class DomainPlayerJoinEvent (
    val player: PlayerContainer,
    val chatMessage: Component?
): DomainEvent()