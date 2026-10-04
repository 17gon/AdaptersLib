package net.craftoriya.adaptersLib.adapter.mapper

import net.craftoriya.adaptersLib.model.ItemContainer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

internal object ItemStackMapper {

    fun toItemStack(container: ItemContainer): ItemStack {
        val material = Material.matchMaterial(container.material)
            ?: error("Unknown material: ${container.material}")

        val item = ItemStack(material, container.count)

        val meta = item.itemMeta
        if (meta != null) {
            if (container.name.isNotEmpty()) meta.setDisplayName(container.name)
            container.properties["lore"]?.let { meta.lore = listOf(it) }
            item.itemMeta = meta
        }

        return item
    }

    fun toItemContainer(item: ItemStack): ItemContainer {
        val data = mutableMapOf<String, String>()
        item.itemMeta?.let { meta ->
            val container = meta.persistentDataContainer
            container.keys.forEach { key ->
                val value: String? = when {
                    container.has(key, PersistentDataType.STRING) -> container.get(key, PersistentDataType.STRING)
                    container.has(key, PersistentDataType.INTEGER) -> container.get(key, PersistentDataType.INTEGER)?.toString()
                    container.has(key, PersistentDataType.DOUBLE) -> container.get(key, PersistentDataType.DOUBLE)?.toString()
                    container.has(key, PersistentDataType.LONG) -> container.get(key, PersistentDataType.LONG)?.toString()
                    container.has(key, PersistentDataType.BYTE) -> container.get(key, PersistentDataType.BYTE)?.toString()
                    else -> null
                }
                if (value != null) data[key.key] = value
            }
        }
        return ItemContainer(
            item.itemMeta?.displayName()?.toString() ?: "",
            item.type.toString(),
            item.amount,
            data
        )
    }
}