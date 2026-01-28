package com.awesoft.ccx.client;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.client.renderer.PCIeRenderer;
import com.awesoft.ccx.client.renderer.PCReaderRenderer;
import com.awesoft.ccx.client.renderer.RackRenderer;
import com.awesoft.ccx.client.renderer.UsbPortRenderer;
import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx.registry.CCXBlocks;
import com.awesoft.ccx_drones.client.CCXDronesClient;
import dan200.computercraft.client.render.TurtleBlockEntityRenderer;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;


public class CCXClient {
    public static void init() {
        CCX.LOGGER.info("CCX: Main Client successfully loaded!");
    }

    public static void BERendererRegister() {
        BlockEntityRendererRegistry.register(CCXBlockEntities.RACK_ENTITY.get(), RackRenderer::new);
        BlockEntityRendererRegistry.register(CCXBlockEntities.PCIE_HUB_ENTITY.get(), PCIeRenderer::new);
        BlockEntityRendererRegistry.register(CCXBlockEntities.PC_READER_ENTITY.get(), PCReaderRenderer::new);
        BlockEntityRendererRegistry.register(CCXBlockEntities.USB_PORT_ENTITY.get(), UsbPortRenderer::new);
        BlockEntityRendererRegistry.register(CCXBlocks.TURTLE_COMMAND_ENTITY.get(), TurtleBlockEntityRenderer::new);
    }
}