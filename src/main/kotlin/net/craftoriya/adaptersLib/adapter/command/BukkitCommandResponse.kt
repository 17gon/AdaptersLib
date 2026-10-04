package net.craftoriya.adaptersLib.adapter.command

import net.craftoriya.adaptersLib.port.CommandResponsePort
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