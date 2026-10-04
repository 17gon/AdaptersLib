package net.craftoriya.adaptersLib.port

import java.util.*

interface IOutlinePort {
    fun show(playerId: UUID, markers: List<OutlineMarker>, ticks: Int)
    fun clear(playerId: UUID)
}

