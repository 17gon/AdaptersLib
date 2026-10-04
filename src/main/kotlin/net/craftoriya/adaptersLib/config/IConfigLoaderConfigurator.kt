package net.craftoriya.adaptersLib.config

import org.spongepowered.configurate.loader.ConfigurationLoader
import java.io.File

interface IConfigLoaderConfigurator {
    fun configure(file: File): ConfigurationLoader<*>
}