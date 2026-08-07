package net.craftoriya.adaptersLib.event.events

import net.craftoriya.adaptersLib.containers.EntityContainer
import net.craftoriya.adaptersLib.containers.PlayerContainer
import net.craftoriya.adaptersLib.containers.RecipeContainer
import net.craftoriya.adaptersLib.event.DomainEvent
import java.util.UUID

class DomainVillagerInteractEvent(
    val player: PlayerContainer,
    val entity: EntityContainer,
    val profession: RecipeContainer.TradeProfession,
    val level: Int
): DomainEvent()