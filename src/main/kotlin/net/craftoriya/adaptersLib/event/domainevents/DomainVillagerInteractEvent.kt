package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.model.EntityContainer
import net.craftoriya.adaptersLib.model.PlayerContainer
import net.craftoriya.adaptersLib.model.RecipeContainer
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainVillagerInteractEvent(
    val player: PlayerContainer,
    val entity: EntityContainer,
    val profession: RecipeContainer.TradeProfession,
    val level: Int
): DomainEvent()