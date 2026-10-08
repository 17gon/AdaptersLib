package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.PlayerContainer

class DomainMenuCloseEvent(
    val player: PlayerContainer,
    val menuId: String
) : DomainEvent()