package net.craftoriya.adaptersLib.config

import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.io.File
import java.util.function.Function
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmName


class ConfigLoader(
    private val folder: File,
    private val extension: String,
    var configurator: IConfigLoaderConfigurator
) {
    fun <T, C : Any> applyContext(cClass: KClass<C>, key: String, mapper: Function<ContextLoader<C>, T>): T {
        validateConfigClass(cClass)
        return mapper.apply(ContextLoader(cClass, key))
    }
    inline fun <T, reified C: Any> applyContext(key: String, mapper: Function<ContextLoader<C>, T>): T {
        return applyContext(C::class, key, mapper)
    }

    fun <C: Any> loadOrSave(cClass: KClass<C>, key: String, defaultConfig: C): C {
        return applyContext(cClass, key) { ctx: ContextLoader<C> ->
            ctx.load() ?: let {
                ctx.save(defaultConfig)
                defaultConfig
            }
        }
    }
    inline fun <reified C: Any> loadOrSave(key: String, defaultConfig: C): C =
        loadOrSave(C::class, key, defaultConfig)

    fun <C: Any> loadOrSave(cClass: KClass<C>, key: String): C {
        validateConfigClass(cClass)
        return loadOrSave(cClass, key, emptyConstructor(cClass).invoke())
    }
    inline fun <reified C: Any> loadOrSave(key: String): C =
        loadOrSave(C::class, key)

    fun <C: Any> load(cClass: KClass<C>, key: String): C? {
        validateConfigClass(cClass)
        val file = getFile(key)
        if (!file.exists()) return null
        val loader: ConfigurationLoader<*> = configurator.configure(file)
        val rootNode: ConfigurationNode = loader.load()
        return rootNode.get(cClass.java)
    }
    inline fun <reified C: Any> load(key: String): C? =
        load(C::class, key)

    fun <C: Any> loadOrDefault(cClass: KClass<C>, key: String, defaultConfig: C): C {
        return load(cClass, key) ?: defaultConfig
    }
    inline fun <reified C: Any> loadOrDefault(key: String, defaultConfig: C): C =
        loadOrDefault(C::class, key, defaultConfig)

    fun <C: Any> loadOrDefault(cClass: KClass<C>, key: String): C {
        validateConfigClass(cClass)
        return loadOrDefault(cClass, key, emptyConstructor(cClass).invoke())
    }
    inline fun <reified C: Any> loadOrDefault(key: String): C =
        loadOrDefault(C::class, key)

    fun <C: Any> save(cClass: KClass<C>, key: String, config: C) {
        validateConfigClass(cClass)
        val file = getFile(key)
        val loader: ConfigurationLoader<*> = configurator.configure(file)
        val node: ConfigurationNode = loader.load()
        node.set<C>(cClass.java, config)
        loader.save(node)
    }
    inline fun <reified C: Any> save(key: String, config: C) =
        save(C::class, key, config)

    fun <C: Any> saveDefault(cClass: KClass<C>, key: String) {
        validateConfigClass(cClass)
        save(cClass, key, emptyConstructor(cClass).invoke())
    }
    inline fun <reified C: Any> saveDefault(key: String) =
        saveDefault(C::class, key)

    private fun <C: Any> emptyConstructor(cClass: KClass<C>): () -> C {
        val constructor = cClass.constructors
            .singleOrNull { it.parameters.all(KParameter::isOptional) }
            ?.apply { isAccessible = true }
            ?: throw IllegalArgumentException("Class should have a single no-arg constructor: $this")
        return { constructor.callBy(emptyMap()) }
    }

    private fun getFile(key: String): File {
        return File(folder, key + extension)
    }

    private fun validateConfigClass(aClass: KClass<*>) {
        validateAnnotation(aClass)
        validateEmptyConstructor(aClass)
    }

    private fun validateAnnotation(aClass: KClass<*>) {
        validate(
            aClass.java.annotations.firstOrNull { it is ConfigSerializable } != null,
            "Your %s class doesn't have a %s annotation",
            aClass.jvmName,
            ConfigSerializable::class.java.getName()
        )
    }

    private fun validateEmptyConstructor(aClass: KClass<*>) {
        validate(
            aClass.constructors
                .singleOrNull { it.parameters.all(KParameter::isOptional) } != null,
            "Your %s class doesn't have an empty constructor",
            aClass.jvmName
        )
    }

    private fun validate(validationCondition: Boolean, errorMessage: String, vararg replacements: Any) {
        check(validationCondition) { String.format(errorMessage, *replacements) }
    }

    inner class ContextLoader<C: Any>(private val cClass: KClass<C>, private val key: String) {
        fun load(): C? {
            return this@ConfigLoader.load(cClass, key)
        }

        fun loadOrDefault(defaultConfig: C): C {
            return this@ConfigLoader.loadOrDefault(cClass, key, defaultConfig)
        }

        fun loadOrDefault(): C {
            return this@ConfigLoader.loadOrDefault(cClass, key)
        }

        fun save(config: C) {
            this@ConfigLoader.save(cClass, key, config)
        }

        fun saveDefault() {
            this@ConfigLoader.saveDefault(cClass, key)
        }
    }
}
