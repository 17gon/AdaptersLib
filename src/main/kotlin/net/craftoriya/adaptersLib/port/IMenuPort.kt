package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.MenuContainer
import java.util.UUID

interface IMenuPort {
    fun open(id: UUID, menuId: String, menu: MenuContainer)
    fun refresh(id: UUID, menu: MenuContainer)
    fun close(id: UUID)
}