package net.craftoriya.adaptersLib.adapter

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class MenuHolder(val menuId: String) : InventoryHolder {
    lateinit var backing: Inventory
    override fun getInventory(): Inventory = backing
}
