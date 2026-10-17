package net.craftoriya.adaptersLib.event.domainevents.villager

import net.craftoriya.adaptersLib.model.entity.EntityContainer
import net.craftoriya.adaptersLib.model.item.RecipeContainer
import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.math.LocationIContainer

class DomainVillagerTickEvent(
    val villager: EntityContainer,
    val profession: RecipeContainer.TradeProfession,
    val level: Int,
    val position: LocationIContainer,
    val jobSite: LocationIContainer?,
    val distanceToJobSite: Double,
    val sleeping: Boolean,
    val worldTime: Long,
    val tick: Long
) : DomainEvent()