package com.awesoft.forge.peripheral;

import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.awesoft.ccx.block.usb.USBBlockEntity;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

public class PeripheralRegistryForge {
    public static void register() {
        ForgeComputerCraftAPI.registerPeripheralProvider(new IPeripheralProvider() {
            @Override
            public LazyOptional<IPeripheral> getPeripheral(Level level, BlockPos blockPos, Direction direction) {
                var be = level.getBlockEntity(blockPos);
                if (be == null) return LazyOptional.empty();
                if (be instanceof PCIeBlockEntity pcie) {
                    return LazyOptional.of(pcie::createPeripheral);
                }
                if (be instanceof USBBlockEntity usb) {
                    return LazyOptional.of(usb::createPeripheral);
                }
                return LazyOptional.empty();
            }
        });
    }
}
