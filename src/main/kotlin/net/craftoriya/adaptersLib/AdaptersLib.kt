package net.craftoriya.adaptersLib

import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.tools.configs.ConfigLoader
import net.craftoriya.adaptersLib.tools.configs.ConfigLoaderConfiguratorBuilder
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.yaml.NodeStyle

class AdaptersLib : JavaPlugin() {

    companion object {
        lateinit var instance: AdaptersLib
            private set

        val eventBus get() = instance._eventBus
        val configLoader get() = instance._configLoader
    }

    private lateinit var _eventBus: DomainEventBus
    private lateinit var _configLoader: ConfigLoader

    override fun onEnable() {
        instance = this
        _eventBus = DomainEventBus()
        _configLoader = ConfigLoader(dataFolder, ".yml",
                ConfigLoaderConfiguratorBuilder.yaml()
                .peekBuilder { it ->
                    it.indent(2)
                    it.nodeStyle(NodeStyle.BLOCK)
                }
                .build()
        )

        logger.info("AdapterLib loaded.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
