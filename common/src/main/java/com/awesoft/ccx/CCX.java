package com.awesoft.ccx;

import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx.registry.CCXBlocks;
import com.awesoft.ccx.registry.CCXItems;
import com.awesoft.ccx.registry.CCXTab;
import com.awesoft.ccx_drones.CCXDrones;
import com.awesoft.ccx_pocket.CCXPocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CCX {
    public static final String NAME = "CCX: Core";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final String MOD_ID = "ccx";

    public static void init() {
        CCXBlocks.register();
        CCXBlockEntities.register();
        CCXItems.register();
        CCXTab.register();
        LOGGER.info("CCX: Main successfully loaded!");
        CCXDrones.init();
        CCXPocket.init();
    }
}
