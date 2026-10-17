package net.craftoriya.adaptersLib.port.ui

import net.craftoriya.adaptersLib.model.outline.OutlineMarker
import java.util.*

interface IOutlinePort {
    fun show(playerId: UUID, markers: List<OutlineMarker>, ticks: Int)
    fun clear(playerId: UUID)
}

