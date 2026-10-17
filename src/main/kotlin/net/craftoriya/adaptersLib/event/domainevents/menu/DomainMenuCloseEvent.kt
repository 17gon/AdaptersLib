package net.craftoriya.adaptersLib.event.domainevents.menu

import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.entity.PlayerContainer

class DomainMenuCloseEvent(
    val player: PlayerContainer,
    val menuId: String
) : DomainEvent()