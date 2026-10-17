package net.craftoriya.adaptersLib.model.entity

import java.util.UUID

data class EntityContainer(
    val id: UUID,
    val type: String,
    val name: String
)
