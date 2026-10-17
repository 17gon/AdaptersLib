package net.craftoriya.adaptersLib.port.ui

import net.craftoriya.adaptersLib.model.menu.MenuContainer
import java.util.UUID

interface IMenuPort {
    fun open(id: UUID, menuId: String, menu: MenuContainer)
    fun refresh(id: UUID, menu: MenuContainer)
    fun close(id: UUID)
}