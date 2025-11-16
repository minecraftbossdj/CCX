package com.awesoft.ccx.lib.periph;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface CCXPeripheralAccess {
    @Nullable
    IPeripheral get(BlockEntity entity, Direction side);
}
