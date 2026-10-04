package net.craftoriya.adaptersLib.event.domainevents

import net.craftoriya.adaptersLib.port.CommandResponsePort
import net.craftoriya.adaptersLib.adapter.command.CommandSenderContainer
import net.craftoriya.adaptersLib.event.DomainEvent

class DomainCommandEvent(
    val sender: CommandSenderContainer,
    val label: String,
    val args: List<String>,
    val response: CommandResponsePort
): DomainEvent()