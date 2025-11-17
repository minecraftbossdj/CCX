package com.awesoft.forge.client;

import com.awesoft.ccx.client.CCXClient;
import com.awesoft.ccx.client.renderer.RackRenderer;
import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx_drones.client.screen.DroneScreen;
import com.awesoft.ccx_drones.registry.CCXDMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCXForgeClient {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        //event.registerBlockEntityRenderer(CCXBlockEntities.RACK_ENTITY.get(), RackRenderer::new);
        CCXClient.BERendererRegister();
    }
    @SubscribeEvent
    public static void registerScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(
                    CCXDMenu.DRONE_MENU.get(),
                    DroneScreen::new
            );
        });
        CCXClient.init();
    }
}
