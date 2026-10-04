package net.craftoriya.adaptersLib.model

import java.util.UUID

data class PlayerContainer(
    val id: UUID,
    val name: String,
    val position: Vec3D,
    val isOnGround: Boolean
)