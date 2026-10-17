package net.craftoriya.adaptersLib.port.world

import net.craftoriya.adaptersLib.model.math.LocationIContainer

interface IBlockDataPort {
    fun get(at: LocationIContainer, key: String): String?
    fun set(at: LocationIContainer, key: String, value: String)
    fun remove(at: LocationIContainer, key: String)
}