package com.awesoft.ccx_upgrades.client

import com.awesoft.ccx_upgrades.CCXUpgrades
import com.awesoft.ccx_upgrades.block.upgradeStation.UpgradeStationEntity
import com.awesoft.ccx_upgrades.client.renderer.UpgradeStationRenderer
import com.awesoft.ccx_upgrades.registry.CCXUBlockEntities
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

object CCXUClient {
    fun init() {
        CCXUpgrades.LOGGER.info("CCX: Upgrades Client successfully loaded!");
    }

    fun BERendererRegister() {
        BlockEntityRendererRegistry.register<UpgradeStationEntity?>(
            CCXUBlockEntities.UPGRADE_STATION_ENTITY?.get(),
            BlockEntityRendererProvider { ctx: BlockEntityRendererProvider.Context? ->
                UpgradeStationRenderer(ctx)
            }
        )
    }
}