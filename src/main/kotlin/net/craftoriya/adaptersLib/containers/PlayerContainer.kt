package net.craftoriya.adaptersLib.containers

import net.craftoriya.adaptersLib.tools.Vec3D
import java.util.UUID

data class PlayerContainer(
    val id: UUID,
    val name: String,
    val position: Vec3D,
    val isOnGround: Boolean
)