package net.craftoriya.adaptersLib.config

import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.loader.AbstractConfigurationLoader
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.function.UnaryOperator

class ConfigLoaderConfiguratorBuilder<L : AbstractConfigurationLoader<*>?, B : AbstractConfigurationLoader.Builder<B, L>?>(
    private val builderSupplier: Supplier<B>
) {
    private val builderAppliers: MutableList<Consumer<B>> = ArrayList()
    private fun apply(consumer: Consumer<B>): ConfigLoaderConfiguratorBuilder<L, B> {
        builderAppliers.add(consumer)
        return this
    }

    fun defaultOptions(defaultOptions: ConfigurationOptions?): ConfigLoaderConfiguratorBuilder<L, B> {
        return apply { b: B -> b!!.defaultOptions(defaultOptions) }
    }

    fun defaultOptions(defaultOptions: UnaryOperator<ConfigurationOptions?>?): ConfigLoaderConfiguratorBuilder<L, B> {
        return apply { b: B -> b!!.defaultOptions(defaultOptions) }
    }

    fun peekBuilder(bConsumer: Consumer<B>): ConfigLoaderConfiguratorBuilder<L, B> {
        return apply(bConsumer)
    }

    fun build(): IConfigLoaderConfigurator {
        val builderAppliers: List<Consumer<B>> = ArrayList(
            builderAppliers
        )
        val builder = builderSupplier.get()
        builderAppliers.forEach(Consumer { c: Consumer<B> -> c.accept(builder) })
        return object : IConfigLoaderConfigurator {
            override fun configure(file: File): ConfigurationLoader<*> {
                return builder!!.file(file)!!.build()!!
            }
        }
    }

    companion object {
        fun yaml(): ConfigLoaderConfiguratorBuilder<YamlConfigurationLoader, YamlConfigurationLoader.Builder> {
            return ConfigLoaderConfiguratorBuilder { YamlConfigurationLoader.builder() }
        }
    }
}
