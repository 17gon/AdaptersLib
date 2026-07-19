package net.craftoriya.adaptersLib.event.events

import net.craftoriya.adaptersLib.containers.RecipeContainer
import net.craftoriya.adaptersLib.event.Cancellable
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainFurnaceSmeltEvent(
    val domainRecipe: RecipeContainer.Cooking,
    override var isCancelled: Boolean = false,
    var extraToConsume: Int = 0
): DomainEvent(), Cancellable

