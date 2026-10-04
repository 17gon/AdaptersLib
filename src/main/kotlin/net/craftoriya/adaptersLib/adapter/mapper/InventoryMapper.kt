package net.craftoriya.adaptersLib.adapter.mapper

import net.craftoriya.adaptersLib.adapter.mapper.ItemStackMapper.toItemContainer
import net.craftoriya.adaptersLib.model.CraftingGridContainer
import net.craftoriya.adaptersLib.model.DataContainer
import net.craftoriya.adaptersLib.model.DataValue
import net.craftoriya.adaptersLib.model.InventoryContainer
import net.craftoriya.adaptersLib.model.InventoryTypeDomain
import net.craftoriya.adaptersLib.model.LocationIContainer
import net.craftoriya.adaptersLib.model.Vec3I
import org.bukkit.block.TileState
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

internal object InventoryMapper {

    fun typeOf(type: InventoryType): InventoryTypeDomain =
        InventoryTypeDomain.entries.firstOrNull { it.name == type.name } ?: InventoryTypeDomain.CRAFTER

    fun craftingGrid(inventory: Inventory): CraftingGridContainer? {
        val type = when (inventory.type) {
            InventoryType.WORKBENCH -> InventoryTypeDomain.WORKBENCH
            InventoryType.CRAFTING -> InventoryTypeDomain.CRAFTING
            else -> return null
        }
        val items = inventory.map { item -> item?.takeUnless { it.type.isAir }?.let { toItemContainer(it) } }
        return CraftingGridContainer(type, items)
    }

    fun toContainer(inventory: Inventory): InventoryContainer {
        val loc = inventory.location
        val data = (inventory.holder as? TileState)?.persistentDataContainer?.let { dataOf(it) } ?: emptyMap()
        return InventoryContainer(
            typeOf(inventory.type),
            inventory.contents.map { it?.let { stack -> toItemContainer(stack) } },
            LocationIContainer(
                loc?.world?.name ?: "",
                Vec3I(loc?.blockX ?: 0, loc?.blockY ?: 0, loc?.blockZ ?: 0)
            ),
            data
        )
    }

    private fun dataOf(pdc: PersistentDataContainer): DataContainer =
        pdc.keys.mapNotNull { key ->
            val value: DataValue? = when {
                pdc.has(key, PersistentDataType.BYTE) -> DataValue.ByteVal(pdc.get(key, PersistentDataType.BYTE)!!)
                pdc.has(key, PersistentDataType.INTEGER) -> DataValue.IntVal(pdc.get(key, PersistentDataType.INTEGER)!!)
                pdc.has(key, PersistentDataType.DOUBLE) -> DataValue.DoubleVal(pdc.get(key, PersistentDataType.DOUBLE)!!)
                pdc.has(key, PersistentDataType.STRING) -> DataValue.StringVal(pdc.get(key, PersistentDataType.STRING)!!)
                else -> null
            }
            value?.let { key.toString() to it }
        }.toMap()
}