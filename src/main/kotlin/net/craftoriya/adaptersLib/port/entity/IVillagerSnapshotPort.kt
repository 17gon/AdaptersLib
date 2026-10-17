package net.craftoriya.adaptersLib.port.entity

import net.craftoriya.adaptersLib.event.domainevents.villager.DomainVillagerTickEvent

interface IVillagerSnapshotPort {
    fun snapshot(): List<DomainVillagerTickEvent>
}