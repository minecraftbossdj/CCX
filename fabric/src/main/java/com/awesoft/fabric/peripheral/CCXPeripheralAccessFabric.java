package com.awesoft.fabric.peripheral;

import com.awesoft.ccx.lib.periph.CCXPeripheralAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class CCXPeripheralAccessFabric implements CCXPeripheralAccess {

    public CCXPeripheralAccessFabric() {}

    @Override
    public @Nullable IPeripheral get(BlockEntity owner, Direction side) {
        if (owner.getLevel() == null || owner.getLevel().isClientSide) return null;
        return PeripheralLookup.get().find(owner.getLevel(), owner.getBlockPos(), owner.getBlockState(), owner, side);
    }
}
