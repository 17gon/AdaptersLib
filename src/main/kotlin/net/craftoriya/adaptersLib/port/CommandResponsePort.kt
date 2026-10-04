package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.adapter.command.CommandSenderContainer

interface CommandResponsePort {
    fun send(sender: CommandSenderContainer, message: String)
    fun sendError(sender: CommandSenderContainer, message: String)
}