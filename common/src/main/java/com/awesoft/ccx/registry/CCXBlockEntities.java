package com.awesoft.ccx.registry;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.pcReader.PCReaderBlockEntity;
import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.awesoft.ccx.block.rack.RackBlockEntity;
import com.awesoft.ccx.block.usb.USBBlockEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CCXBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(CCX.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<RackBlockEntity>> RACK_ENTITY =
            BLOCK_ENTITIES.register("rack", () ->
                    BlockEntityType.Builder.of(RackBlockEntity::new, CCXBlocks.RACK.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<PCIeBlockEntity>> PCIE_HUB_ENTITY =
            BLOCK_ENTITIES.register("pcie_hub", () ->
                    BlockEntityType.Builder.of(PCIeBlockEntity::new, CCXBlocks.PCIE_HUB.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<PCReaderBlockEntity>> PC_READER_ENTITY =
            BLOCK_ENTITIES.register("pc_reader", () ->
                    BlockEntityType.Builder.of(PCReaderBlockEntity::new, CCXBlocks.PC_READER.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<USBBlockEntity>> USB_PORT_ENTITY =
            BLOCK_ENTITIES.register("usb_port", () ->
                    BlockEntityType.Builder.of(USBBlockEntity::new, CCXBlocks.USB_PORT.get()).build(null));

    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
