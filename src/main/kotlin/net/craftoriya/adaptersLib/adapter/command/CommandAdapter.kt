package net.craftoriya.adaptersLib.adapter.command

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.event.domainevents.DomainCommandEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandAdapter(
    private val bus: DomainEventBus,
    private val commandLabel: String
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
        return emptyList()
    }

    override fun canUse(sender: CommandSender): Boolean = true
}