package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.MenuClick
import net.craftoriya.adaptersLib.model.PlayerContainer

class DomainMenuClickEvent(
    val player: PlayerContainer,
    val menuId: String,
    val slot: Int,
    val click: MenuClick
) : DomainEvent()

