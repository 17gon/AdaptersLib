package net.craftoriya.adaptersLib.event

abstract class DomainEvent

interface Cancellable {
    var isCancelled: Boolean
}