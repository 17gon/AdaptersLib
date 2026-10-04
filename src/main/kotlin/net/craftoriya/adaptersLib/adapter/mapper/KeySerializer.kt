package net.craftoriya.adaptersLib.adapter.mapper

import net.kyori.adventure.key.Key
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.typedSet
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

object KeySerializer : TypeSerializer<Key?> {
    override fun deserialize(type: Type, node: ConfigurationNode): Key? = node.string?.let {
        Key.key(it)
    }

    override fun serialize(type: Type, obj: Key?, node: ConfigurationNode) {
        node.typedSet<String?>(obj?.asString())
    }
}