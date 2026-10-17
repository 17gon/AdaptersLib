package net.craftoriya.adaptersLib.port.world

import net.craftoriya.adaptersLib.model.item.ItemContainer
import net.craftoriya.adaptersLib.model.math.LocationIContainer
import net.craftoriya.adaptersLib.model.item.SlotItem

interface IContainerPort {
    fun contents(at: LocationIContainer): List<ItemContainer>
    fun slots(at: LocationIContainer): List<SlotItem>
    fun take(at: LocationIContainer, filter: (ItemContainer) -> Boolean, max: Int): List<ItemContainer>
    fun put(at: LocationIContainer, items: List<ItemContainer>): List<ItemContainer>
}