package net.craftoriya.adaptersLib.adapter.command

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.domainevents.player.DomainCommandEvent
import net.craftoriya.adaptersLib.model.command.CommandContext
import net.craftoriya.adaptersLib.model.command.CommandSpec
import net.craftoriya.adaptersLib.model.math.Vec3I
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandAdapter(
    private val bus: DomainEventBus,
    private val commandLabel: String,
    private val spec: CommandSpec? = null
) : BasicCommand {
    override fun execute(source: CommandSourceStack, args: Array<String>) {
        val sender = source.sender
        val container = CommandSenderContainer(
            sender.name,
            sender is Player,
            (sender as? Player)?.uniqueId
        )
        bus.publish(DomainCommandEvent(
            container,
            commandLabel,
            args.toList(),
            BukkitCommandResponse(sender)
        ))
    }

    override fun suggest(source: CommandSourceStack, args: Array<String>): Collection<String> {
        val spec = spec ?: return emptyList()
        return runCatching { spec.suggest(args.toList(), context(source.sender)) }.getOrDefault(emptyList())
    }

    override fun canUse(sender: CommandSender): Boolean = true

    private fun context(sender: CommandSender): CommandContext {
        val player = sender as? Player ?: return CommandContext(null, null)
        val pos = player.location.let { Vec3I(it.blockX, it.blockY, it.blockZ) }
        val target = if (Bukkit.isPrimaryThread()) {
            player.getTargetBlockExact(TARGET_RANGE)?.let { Vec3I(it.x, it.y, it.z) }
        } else null
        return CommandContext(target, pos)
    }

    private companion object {
        const val TARGET_RANGE = 8
    }
}