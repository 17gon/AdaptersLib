package net.craftoriya.adaptersLib.port

import net.craftoriya.adaptersLib.event.domainevents.DomainVillagerTickEvent

interface IVillagerSnapshotPort {
    fun snapshot(): List<DomainVillagerTickEvent>
}