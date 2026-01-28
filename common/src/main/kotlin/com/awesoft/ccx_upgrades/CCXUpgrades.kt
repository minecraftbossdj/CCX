package com.awesoft.ccx_upgrades

import com.awesoft.ccx_upgrades.registry.CCXUBlockEntities
import com.awesoft.ccx_upgrades.registry.CCXUBlocks
import com.awesoft.ccx_upgrades.registry.CCXUItems
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger



object CCXUpgrades {
    val NAME: String = "CCX: Upgrades"
    val LOGGER: Logger = LogManager.getLogger(NAME)
    val MOD_ID: String = "ccx_upgrades"

    fun init() { //use fun so java can call it
        CCXUBlocks.BLOCKS.register()
        CCXUItems.ITEMS.register()
        CCXUBlockEntities.BLOCK_ENTITIES.register()
    }
}