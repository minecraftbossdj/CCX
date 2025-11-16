package com.awesoft.forge.peripheral;

import com.awesoft.ccx.lib.periph.CCXPeripheralAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.impl.Peripherals;
import io.netty.util.internal.UnstableApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class CCXPeripheralAccessForge implements CCXPeripheralAccess {

    public CCXPeripheralAccessForge() {}

    @Override
    public @Nullable IPeripheral get(BlockEntity owner, Direction side) {
        if (owner.getLevel() == null || owner.getLevel().isClientSide) return null;
        return Peripherals.getPeripheral((ServerLevel) owner.getLevel(), owner.getBlockPos(), side, () -> {});
    }
}
