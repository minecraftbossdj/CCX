package com.awesoft.forge;

import com.awesoft.ccx.client.CCXClient;
import com.awesoft.ccx.lib.periph.PeripheralAccessProvider;
import com.awesoft.ccx_drones.CCXDrones;
import com.awesoft.ccx_drones.client.screen.DroneScreen;
import com.awesoft.ccx_drones.registry.CCXDMenu;
import com.awesoft.ccx_pocket.CCXPocket;
import com.awesoft.ccx_pocket.CCXPocketClient;
import com.awesoft.forge.peripheral.CCXPeripheralAccessForge;
import com.awesoft.forge.peripheral.PeripheralRegistryForge;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.awesoft.ccx.CCX;

@Mod(CCX.MOD_ID)
public final class CCXForge {
    public CCXForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(CCX.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        EventBuses.registerModEventBus(CCXDrones.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        EventBuses.registerModEventBus(CCXPocket.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        PeripheralAccessProvider.register(new CCXPeripheralAccessForge());

        // Run our common setup.
        CCX.init();
        PeripheralRegistryForge.register();
    }
}
