package net.craftoriya.adaptersLib.event.domainevents.menu

import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.menu.MenuClick
import net.craftoriya.adaptersLib.model.entity.PlayerContainer

class DomainMenuClickEvent(
    val player: PlayerContainer,
    val menuId: String,
    val slot: Int,
    val click: MenuClick
) : DomainEvent()

