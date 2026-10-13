package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.BlockContainer
import net.craftoriya.adaptersLib.model.ItemContainer
import java.util.UUID

class DomainBlockDropEvent(
    val block: BlockContainer,
    val breaker: UUID?,
    val drops: MutableList<ItemContainer>
) : DomainEvent()