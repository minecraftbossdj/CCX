package com.awesoft.ccx_drones.client;

import com.awesoft.ccx_drones.CCXDrones;
import com.awesoft.ccx_drones.client.render.DroneEntityRenderer;
import com.awesoft.ccx_drones.registry.CCXDEntities;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class CCXDronesClient {
    public static final ModelLayerLocation MODEL_DRONE_LAYER = new ModelLayerLocation(new ResourceLocation("ccx_drones", "drone"), "main");

    public static void init() {
        EntityRendererRegistry.register(CCXDEntities.DRONE_ENTITY, DroneEntityRenderer::new);
        EntityModelLayerRegistry.register(MODEL_DRONE_LAYER, DroneEntityModel::getTexturedData);

        CCXDrones.LOGGER.info("CCX: Drones Client successfully loaded!");
    }
}
