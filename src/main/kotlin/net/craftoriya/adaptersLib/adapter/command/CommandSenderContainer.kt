package net.craftoriya.adaptersLib.adapter.command

import java.util.UUID

data class CommandSenderContainer(
    val name: String,
    val isPlayer: Boolean,
    val playerId: UUID?
)