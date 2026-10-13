package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.ItemContainer
import net.craftoriya.adaptersLib.model.LocationIContainer
import net.craftoriya.adaptersLib.model.SlotItem

interface IContainerPort {
    fun contents(at: LocationIContainer): List<ItemContainer>
    fun slots(at: LocationIContainer): List<SlotItem>
    fun take(at: LocationIContainer, filter: (ItemContainer) -> Boolean, max: Int): List<ItemContainer>
    fun put(at: LocationIContainer, items: List<ItemContainer>): List<ItemContainer>
}