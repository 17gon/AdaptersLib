package net.craftoriya.adaptersLib.port

interface ISchedulerPort {
    fun currentTick(): Long
    fun runLater(delayTicks: Long, task: () -> Unit)
    fun runRepeating(delayTicks: Long, periodTicks: Long, task: () -> Boolean)
}