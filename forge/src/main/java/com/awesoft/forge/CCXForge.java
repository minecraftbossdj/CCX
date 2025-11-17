package com.awesoft.forge;

import com.awesoft.ccx.lib.periph.PeripheralAccessProvider;
import com.awesoft.ccx_drones.CCXDrones;
import com.awesoft.forge.peripheral.CCXPeripheralAccessForge;
import com.awesoft.forge.peripheral.PeripheralRegistryForge;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.awesoft.ccx.CCX;

@Mod(CCX.MOD_ID)
public final class CCXForge {
    public CCXForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(CCX.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        EventBuses.registerModEventBus(CCXDrones.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        PeripheralAccessProvider.register(new CCXPeripheralAccessForge());

        // Run our common setup.
        CCX.init();
        PeripheralRegistryForge.register();
    }
}
