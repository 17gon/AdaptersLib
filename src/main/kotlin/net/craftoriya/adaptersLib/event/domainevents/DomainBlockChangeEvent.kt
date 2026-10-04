package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.event.DomainEvent
import net.craftoriya.adaptersLib.model.BlockContainer

class DomainBlockChangeEvent(
    val block: BlockContainer,
    val change: BlockChange
) : DomainEvent()

enum class BlockChange { BREAK, PLACE }
