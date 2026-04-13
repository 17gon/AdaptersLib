package net.craftoriya.adaptersLib.command

import net.craftoriya.adaptersLib.command.CommandSenderContainer

interface CommandResponsePort {
    fun send(sender: CommandSenderContainer, message: String)
    fun sendError(sender: CommandSenderContainer, message: String)
}