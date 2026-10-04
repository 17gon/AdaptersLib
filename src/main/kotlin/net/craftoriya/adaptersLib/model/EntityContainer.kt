package net.craftoriya.adaptersLib.model

import java.util.UUID

data class EntityContainer(
    val id: UUID,
    val type: String,
    val name: String
)
