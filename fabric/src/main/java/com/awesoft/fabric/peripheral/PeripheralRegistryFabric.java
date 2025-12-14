package com.awesoft.fabric.peripheral;

import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.awesoft.ccx.block.pcie.PCIePeripheral;
import com.awesoft.ccx.block.usb.USBPeripheral;
import com.awesoft.ccx.registry.CCXBlockEntities;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class PeripheralRegistryFabric {
    public static void register() {
        PeripheralLookup.get().registerForBlockEntity((a, b)->new PCIePeripheral(a), CCXBlockEntities.PCIE_HUB_ENTITY.get());
        PeripheralLookup.get().registerForBlockEntity((a, b)->new USBPeripheral(a), CCXBlockEntities.USB_PORT_ENTITY.get());
    }
}
