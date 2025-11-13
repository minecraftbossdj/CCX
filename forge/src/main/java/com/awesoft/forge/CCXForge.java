package com.awesoft.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.awesoft.ccx.CCX;

@Mod(CCX.MOD_ID)
public final class CCXForge {
    public CCXForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(CCX.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        CCX.init();
    }
}
