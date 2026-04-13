package net.craftoriya.adaptersLib.command

import java.util.UUID

data class CommandSenderContainer(
    val name: String,
    val isPlayer: Boolean,
    val playerId: UUID?
)