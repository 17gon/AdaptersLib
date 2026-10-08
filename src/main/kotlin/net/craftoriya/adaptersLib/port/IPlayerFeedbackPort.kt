package net.craftoriya.adaptersLib.port

import java.util.UUID

interface IPlayerFeedbackPort {
    fun actionBar(id: UUID, text: String)
    fun glowHeldItem(id: UUID, glow: Boolean)
}