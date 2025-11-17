package com.awesoft.ccx.client;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.client.renderer.PCIeRenderer;
import com.awesoft.ccx.client.renderer.PCReaderRenderer;
import com.awesoft.ccx.client.renderer.RackRenderer;
import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx_drones.client.CCXDronesClient;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;


public class CCXClient {
    public static void init() {
        CCX.LOGGER.info("CCX: Main Client successfully loaded!");
        CCXDronesClient.init();
    }

    public static void BERendererRegister() {
        BlockEntityRendererRegistry.register(CCXBlockEntities.RACK_ENTITY.get(), RackRenderer::new);
        BlockEntityRendererRegistry.register(CCXBlockEntities.PCIE_HUB_ENTITY.get(), PCIeRenderer::new);
        BlockEntityRendererRegistry.register(CCXBlockEntities.PC_READER_ENTITY.get(), PCReaderRenderer::new);
    }
}