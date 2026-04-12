package net.craftoriya.adaptersLib

import net.craftoriya.adaptersLib.event.DomainEventBus
import org.bukkit.plugin.java.JavaPlugin

class AdaptersLib : JavaPlugin() {

    companion object {
        lateinit var eventBus: DomainEventBus
            private set
    }

    override fun onEnable() {
        eventBus = DomainEventBus()
        logger.info("AdapterLib loaded.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
