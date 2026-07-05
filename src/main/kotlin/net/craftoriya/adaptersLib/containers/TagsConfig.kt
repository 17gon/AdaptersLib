package net.craftoriya.adaptersLib.containers

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TagsConfig(val tags: Map<String, List<String>> = emptyMap())