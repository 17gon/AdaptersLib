package net.craftoriya.adaptersLib.event.domainevents.villager

import net.craftoriya.adaptersLib.model.entity.EntityContainer
import net.craftoriya.adaptersLib.model.entity.PlayerContainer
import net.craftoriya.adaptersLib.model.item.RecipeContainer
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainVillagerInteractEvent(
    val player: PlayerContainer,
    val entity: EntityContainer,
    val profession: RecipeContainer.TradeProfession,
    val level: Int
): DomainEvent()