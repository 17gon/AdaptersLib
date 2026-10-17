package net.craftoriya.adaptersLib.event.domainevents.block

import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.block.BlockChange
import net.craftoriya.adaptersLib.model.block.BlockContainer

class DomainBlockChangeEvent(
    val block: BlockContainer,
    val change: BlockChange
) : DomainEvent()
