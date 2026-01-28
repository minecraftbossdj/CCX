package com.awesoft.ccx_upgrades.registry

import com.awesoft.ccx_upgrades.CCXUpgrades
import com.awesoft.ccx_upgrades.block.upgradeStation.UpgradeStation
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import java.util.function.Supplier

object CCXUBlocks {
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(CCXUpgrades.MOD_ID, Registries.BLOCK)

    val UPGRADE_STATION: RegistrySupplier<Block> = BLOCKS.register("upgrade_station") {
        UpgradeStation(BlockBehaviour.Properties.of())
    }

    fun register() {
        BLOCKS.register()
    }
}