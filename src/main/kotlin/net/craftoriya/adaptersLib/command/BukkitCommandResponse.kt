package net.craftoriya.adaptersLib.command

import net.craftoriya.adaptersLib.command.CommandSenderContainer
import org.bukkit.command.CommandSender

internal class BukkitCommandResponse(
    private val sender: CommandSender
) : CommandResponsePort {

    override fun send(sender: CommandSenderContainer, message: String) {
        this.sender.sendMessage(message)
    }

    override fun sendError(sender: CommandSenderContainer, message: String) {
        this.sender.sendMessage("§c$message")
    }
}