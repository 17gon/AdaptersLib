package net.craftoriya.adaptersLib.adapter.system

import net.craftoriya.adaptersLib.port.system.ISchedulerPort
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer

class BukkitSchedulerAdapter(private val plugin: Plugin) : ISchedulerPort {

    override fun currentTick(): Long = Bukkit.getCurrentTick().toLong()

    override fun runLater(delayTicks: Long, task: () -> Unit) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { task() }, delayTicks)
    }

    override fun runRepeating(delayTicks: Long, periodTicks: Long, task: () -> Boolean) {
        Bukkit.getScheduler().runTaskTimer(
            plugin,
            Consumer<BukkitTask> { t ->
                val tm = Bukkit.getServerTickManager()
                if (!(tm.isFrozen && !tm.isStepping) && !task()) t.cancel()
            },
            delayTicks, periodTicks
        )
    }
}