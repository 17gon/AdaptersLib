package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.model.LocationIContainer

interface IBlockDataPort {
    fun get(at: LocationIContainer, key: String): String?
    fun set(at: LocationIContainer, key: String, value: String)
    fun remove(at: LocationIContainer, key: String)
}