package com.awesoft.ccx.client;

import com.awesoft.ccx.client.renderer.RackRenderer;
import com.awesoft.ccx.registry.CCXBlockEntities;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;


public class CCXClient {
    public static void init() {

    }

    public static void BERendererRegister() {
        BlockEntityRendererRegistry.register(CCXBlockEntities.RACK_ENTITY.get(), RackRenderer::new);
    }
}