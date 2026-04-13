package net.craftoriya.adaptersLib.event.events

import net.craftoriya.adaptersLib.command.CommandResponsePort
import net.craftoriya.adaptersLib.command.CommandSenderContainer
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainCommandEvent(
    val sender: CommandSenderContainer,
    val label: String,
    val args: List<String>,
    val response: CommandResponsePort
): DomainEvent()