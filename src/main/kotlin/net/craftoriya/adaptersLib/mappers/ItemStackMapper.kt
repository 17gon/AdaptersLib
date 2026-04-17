package net.craftoriya.adaptersLib.mappers

import net.craftoriya.adaptersLib.containers.ItemContainer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

internal object ItemStackMapper {

    fun toItemStack(container: ItemContainer): ItemStack {
        val material = Material.matchMaterial(container.material)
            ?: error("Unknown material: ${container.material}")

        val item = ItemStack(material, container.count)

        val meta = item.itemMeta
        if (meta != null) {
            if (container.name.isNotEmpty()) {
                meta.setDisplayName(container.name)
            }

            // simple properties mapping (you can expand later)
            container.properties.forEach { (key, value) ->
                // example placeholder logic
                if (key == "lore") {
                    meta.lore = listOf(value)
                }
            }

            item.itemMeta = meta
        }

        return item
    }
}