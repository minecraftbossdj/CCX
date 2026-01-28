package com.awesoft.ccx_upgrades.registry

import com.awesoft.ccx.CCX
import com.awesoft.ccx_upgrades.CCXUpgrades
import com.awesoft.ccx_upgrades.registry.CCXUBlocks.UPGRADE_STATION
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item

object CCXUItems {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(CCXUpgrades.MOD_ID, Registries.ITEM)

    val UPGRADE_STATION_ITEM: RegistrySupplier<Item> = ITEMS.register("upgrade_station") {
        BlockItem(UPGRADE_STATION.get(), Item.Properties())
    }

    fun register() {
        ITEMS.register()
    }
}