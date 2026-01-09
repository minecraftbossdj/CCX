package com.awesoft.ccx_drones;

import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx.registry.CCXBlocks;
import com.awesoft.ccx.registry.CCXItems;
import com.awesoft.ccx.registry.CCXTab;
import com.awesoft.ccx_drones.registry.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CCXDrones {
    public static final String NAME = "CCX: Drones";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final String MOD_ID = "ccx_drones";

    public static void init() {
        CCXDEntities.register();
        CCXDItems.register();
        CCXDComponents.register();
        CCXDMenu.register();
        CCXDTab.register();
        LOGGER.info("CCX: Drones successfully loaded!");
    } //im ngl, most this code is ported over from "my" other mod, CC:Drones+

}
