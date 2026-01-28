package com.awesoft.forge.client;

import com.awesoft.ccx.client.CCXClient;
import com.awesoft.ccx.client.renderer.RackRenderer;
import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx_drones.client.DroneEntityModel;
import com.awesoft.ccx_drones.client.render.DroneEntityRenderer;
import com.awesoft.ccx_drones.client.screen.DroneScreen;
import com.awesoft.ccx_drones.registry.CCXDEntities;
import com.awesoft.ccx_drones.registry.CCXDMenu;
import com.awesoft.ccx_pocket.CCXPocketClient;
import com.awesoft.ccx_upgrades.client.CCXUClient;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.awesoft.ccx_drones.client.CCXDronesClient.MODEL_DRONE_LAYER;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCXForgeClient {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        //event.registerBlockEntityRenderer(CCXBlockEntities.RACK_ENTITY.get(), RackRenderer::new);
        CCXClient.BERendererRegister();
        CCXUClient.INSTANCE.BERendererRegister();
    }

    @SubscribeEvent
    public static void startup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(
                    CCXDMenu.DRONE_MENU.get(),
                    DroneScreen::new
            );
        });
        CCXClient.init();
        CCXPocketClient.init();
        CCXUClient.INSTANCE.init();
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CCXDEntities.DRONE_ENTITY.get(), (DroneEntityRenderer::new));
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MODEL_DRONE_LAYER, DroneEntityModel::getTexturedData);
    }
}
