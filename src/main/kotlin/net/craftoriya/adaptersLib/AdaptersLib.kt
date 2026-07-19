package net.craftoriya.adaptersLib

import net.craftoriya.adaptersLib.event.DomainEventBus
import net.craftoriya.adaptersLib.tools.configs.ConfigLoader
import net.craftoriya.adaptersLib.tools.configs.ConfigLoaderConfiguratorBuilder
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.util.NamingSchemes
import org.spongepowered.configurate.yaml.NodeStyle
import java.io.File

class AdaptersLib : JavaPlugin() {

    companion object {
        lateinit var instance: AdaptersLib
            private set
        val eventBus get() = instance._eventBus
        fun configLoader(dataFolder: File) = ConfigLoader(dataFolder, ".yml",
            ConfigLoaderConfiguratorBuilder.yaml()
                .defaultOptions { opts ->
                    opts?.serializers { it.registerAnnotatedObjects(
                        ObjectMapper.factoryBuilder()
                            .defaultNamingScheme(NamingSchemes.PASSTHROUGH)
                            .build()
                    ) }
                }
                .peekBuilder {
                    it.indent(2)
                    it.nodeStyle(NodeStyle.BLOCK)
                }
                .build()
        )
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
