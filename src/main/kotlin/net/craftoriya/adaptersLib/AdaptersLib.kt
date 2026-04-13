package net.craftoriya.adaptersLib

import net.craftoriya.adaptersLib.event.DomainEventBus
import org.bukkit.plugin.java.JavaPlugin

class AdaptersLib : JavaPlugin() {

    companion object {
        lateinit var instance: AdaptersLib
            private set

        val eventBus get() = instance._eventBus
    }

    private lateinit var _eventBus: DomainEventBus

    override fun onEnable() {
        instance = this
        _eventBus = DomainEventBus()
        logger.info("AdapterLib loaded.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
