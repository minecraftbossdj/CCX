package com.awesoft.fabric.client;

import com.awesoft.ccx.client.CCXClient;
import com.awesoft.ccx.client.renderer.RackRenderer;
import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx_drones.client.CCXDronesClient;
import com.awesoft.ccx_drones.client.screen.DroneScreen;
import com.awesoft.ccx_drones.registry.CCXDMenu;
import com.awesoft.ccx_pocket.CCXPocket;
import com.awesoft.ccx_pocket.CCXPocketClient;
import com.awesoft.ccx_upgrades.client.CCXUClient;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public final class CCXFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //BlockEntityRendererRegistry.register(CCXBlockEntities.RACK_ENTITY.get(), RackRenderer::new);
        CCXClient.BERendererRegister();
        CCXUClient.INSTANCE.BERendererRegister();
        MenuScreens.register(
                CCXDMenu.DRONE_MENU.get(),
                DroneScreen::new
        );
        CCXClient.init();
        CCXDronesClient.init();
        CCXPocketClient.init();
        CCXUClient.INSTANCE.init();
    }
}
