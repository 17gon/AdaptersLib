package net.craftoriya.adaptersLib.event

import kotlin.reflect.KClass

class DomainEventBus {
    private val handlers = mutableMapOf<KClass<*>, MutableList<HandlerEntry<*>>>()

    fun <T : DomainEvent> on(
        type: KClass<T>,
        priority: HandlerPriority = HandlerPriority.NORMAL,
        handler: (T) -> Unit
    ) {
        val entry = HandlerEntry(priority, handler)
        handlers.getOrPut(type) { mutableListOf() }.also { list ->
            list.add(entry)
            list.sortBy { it.priority.ordinal }
        }
    }

    inline fun <reified T : DomainEvent> on(
        priority: HandlerPriority = HandlerPriority.NORMAL,
        noinline handler: (T) -> Unit
    ) = on(T::class, priority, handler)

    @Suppress("UNCHECKED_CAST")
    fun publish(event: DomainEvent) {
        val list: MutableList<HandlerEntry<*>> = handlers[event::class] ?: return
        for (entry in list) {
            if (event is Cancellable && event.isCancelled) break
            (entry.handler as (DomainEvent) -> Unit).invoke(event)
        }
    }
}

data class HandlerEntry<T>(
    val priority: HandlerPriority,
    val handler: (T) -> Unit
)
enum class HandlerPriority { LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR }