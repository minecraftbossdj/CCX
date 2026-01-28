package com.awesoft.ccx_upgrades.registry

import com.awesoft.ccx_upgrades.CCXUpgrades
import com.awesoft.ccx_upgrades.block.upgradeStation.UpgradeStationEntity
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Supplier

object CCXUBlockEntities {
    val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>?> = DeferredRegister.create(CCXUpgrades.MOD_ID, Registries.BLOCK_ENTITY_TYPE)

    val UPGRADE_STATION_ENTITY: RegistrySupplier<BlockEntityType<UpgradeStationEntity>> =
        BLOCK_ENTITIES.register(
            "upgrade_station",
            Supplier {
                BlockEntityType.Builder.of(BlockEntityType.BlockEntitySupplier { pPos: BlockPos?, pBlockState: BlockState? ->
                    UpgradeStationEntity(
                        pPos,
                        pBlockState
                    )
                }, CCXUBlocks.UPGRADE_STATION.get()).build(null)
            })

    fun register() {
        BLOCK_ENTITIES.register()
    }
}