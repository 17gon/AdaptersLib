package net.craftoriya.adaptersLib.adapter.mapper

import net.craftoriya.adaptersLib.model.PlayerContainer
import net.craftoriya.adaptersLib.model.Vec3D
import org.bukkit.entity.Player

internal object PlayerMapper {
    fun toContainer(player: Player) = PlayerContainer(
        player.uniqueId,
        player.name,
        Vec3D(player.location.x, player.location.y, player.location.z),
        player.isOnGround
    )
}