package net.craftoriya.adaptersLib.listeners

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.craftoriya.adaptersLib.containers.PlayerContainer
import net.craftoriya.adaptersLib.containers.Vec3D
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.events.PlayerJumpDomainEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import java.util.EventListener

class PaperEventListener(private val bus: DomainEventBus): EventListener {


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)//I have questions why I should listen to those
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player
        val pos = Vec3D(player.location.x, player.location.y, player.location.z)
        val playerContainer = PlayerContainer(player.uniqueId, player.name, pos, player.isOnGround)
        val domainEvent = PlayerJumpDomainEvent(playerContainer)

        bus.publish(domainEvent)
        if (domainEvent.isCancelled) {
            event.isCancelled = true
        }
    }
}