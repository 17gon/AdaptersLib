package net.craftoriya.adaptersLib.event.domainevents.block

import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.block.BlockContainer
import net.craftoriya.adaptersLib.model.item.ItemContainer
import java.util.UUID

class DomainBlockDropEvent(
    val block: BlockContainer,
    val breaker: UUID?,
    val drops: MutableList<ItemContainer>
) : DomainEvent()