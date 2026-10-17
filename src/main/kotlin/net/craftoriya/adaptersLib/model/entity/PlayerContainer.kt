package net.craftoriya.adaptersLib.model.entity

import net.craftoriya.adaptersLib.model.math.Vec3D
import java.util.UUID

data class PlayerContainer(
    val id: UUID,
    val name: String,
    val position: Vec3D,
    val isOnGround: Boolean
)