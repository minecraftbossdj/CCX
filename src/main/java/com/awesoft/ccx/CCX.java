package com.awesoft.ccx;

import com.awesoft.ccx.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("ccx")
public class CCX {

    public static final String NAME = "CCX: Core";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final String MODID = "ccx";


    public CCX() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TabInit.CREATIVE_MODE_TABS.register(bus);
        CCXItems.register(bus);
        CCXBlocks.register(bus);
        CCXBlockEntities.register(bus);
        APIRegistry.register();
        CCXConfig.register(ModLoadingContext.get());
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::onClientSetup);
        LOGGER.info("CCX: Main Loading Completed.");
        //TODO: find a good way to dynamically load fake "main classes" (for like CCXPocket.class)
    }

    private void onClientSetup(final FMLClientSetupEvent event) {

    }
}
