package com.awesoft.ccx_pocket;

import com.awesoft.ccx_pocket.networking.hmd.OpenHMDNetwork;
import com.awesoft.ccx_pocket.networking.pocket_pack.OpenPackNetwork;
import com.awesoft.ccx_pocket.registry.CCXPComponents;
import com.awesoft.ccx_pocket.registry.CCXPItems;
import com.awesoft.ccx_pocket.registry.CCXPTab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CCXPocket {
    public static final String NAME = "CCX: Pocket";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final String MOD_ID = "ccx_pocket";

    public static void init() {
        OpenHMDNetwork.register();
        OpenPackNetwork.register();
        CCXPComponents.register();
        CCXPItems.register();
        CCXPTab.register();
        LOGGER.info("CCX: Pocket successfully loaded!");
    }
}
