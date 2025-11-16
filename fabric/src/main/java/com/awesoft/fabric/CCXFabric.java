package com.awesoft.fabric;

import com.awesoft.ccx.lib.periph.PeripheralAccessProvider;
import com.awesoft.fabric.peripheral.CCXPeripheralAccessFabric;
import com.awesoft.fabric.peripheral.PeripheralRegistryFabric;
import net.fabricmc.api.ModInitializer;

import com.awesoft.ccx.CCX;

public final class CCXFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        PeripheralAccessProvider.register(new CCXPeripheralAccessFabric());

        // Run our common setup.
        CCX.init();
        PeripheralRegistryFabric.register();
    }
}
