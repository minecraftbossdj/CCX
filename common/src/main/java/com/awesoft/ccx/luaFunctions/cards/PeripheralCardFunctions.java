package com.awesoft.ccx.luaFunctions.cards;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.awesoft.ccx.lib.DirectionLib;
import com.awesoft.ccx.lib.periph.PeripheralAccessProvider;
import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx.registry.CCXItems;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.util.PeripheralHelpers;
import dan200.computercraft.shared.computer.blocks.ComputerBlockEntity;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.platform.ComponentAccess;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.Platform;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeripheralCardFunctions {

    public Map<String, Object> functions = new HashMap<>();

    private PCIeBlockEntity blkEnt;
    private int slot;
    private List<IComputerAccess> computers = new ArrayList<>();
    private ServerLevel level;
    private BlockEntity periphBlockEntity;

    public PeripheralCardFunctions(PCIeBlockEntity entFake, List<IComputerAccess> comps, int slot, BlockEntity periphBlockEntity) {
        this.blkEnt = entFake;
        this.slot = slot;
        this.computers = comps;
        this.periphBlockEntity = periphBlockEntity;
    }

    //this goes unused as it does NOT want to attach peripheral correctly.

    public ILuaFunction attachPeripheral = args -> {
        if (blkEnt.getLevel() == null || blkEnt.getLevel().isClientSide) return null;
        CCX.LOGGER.info("ARE WE ALIVE but ENTITY");

        if (!blkEnt.getLevel().hasChunkAt(periphBlockEntity.getBlockPos())) return MethodResult.of(false, "Block pos not loaded!");
        BlockPos pos = periphBlockEntity.getBlockPos();
        if (periphBlockEntity == null) {
            CCX.LOGGER.info(blkEnt+" yo momma");
            return MethodResult.of(false, "Not a valid peripheral! (debug, pos: " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + ")");
        }
        IPeripheral periph = PeripheralAccessProvider.get().get(periphBlockEntity, Direction.NORTH);
        CCX.LOGGER.info(periph+" - blk ent periph");
        if (periph == null) return MethodResult.of(false, "Not a valid peripheral! (debug: actual peripheral)");

        CCX.LOGGER.info(pos);


        for (IComputerAccess comp : computers) {
            CCX.LOGGER.info("comp: "+comp);
            periph.attach(comp);
        }

        return MethodResult.of(true, "Peripheral Attached!");

    };


    public ILuaFunction detachPeripheral = args -> {
        if (blkEnt.getLevel() == null || blkEnt.getLevel().isClientSide) return null;
        CCX.LOGGER.info("ARE WE ALIVE but ENTITY");

        if (!blkEnt.getLevel().hasChunkAt(periphBlockEntity.getBlockPos())) return MethodResult.of(false, "Block pos not loaded!");
        BlockPos pos = periphBlockEntity.getBlockPos();
        if (periphBlockEntity == null) {
            CCX.LOGGER.info(blkEnt+" yo momma");
            return MethodResult.of(false, "Not a valid peripheral! (debug, pos: " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + ")");
        }
        IPeripheral periph = PeripheralAccessProvider.get().get(periphBlockEntity, Direction.NORTH);
        CCX.LOGGER.info(periph);
        if (periph == null) return MethodResult.of(false, "Not a valid peripheral! (debug: actual peripheral)");

        for (IComputerAccess comp : computers) {
            CCX.LOGGER.info("yo momma 2"+comp);
            periph.detach(comp);
        }

        CCX.LOGGER.info("what the simga");

        return MethodResult.of(true, "Peripheral Detached!");
    };

    public Map<String, Object> getFunctions() {
        if (blkEnt.getLevel() == null || blkEnt.getLevel().isClientSide) return null;
        ServerLevel level = (ServerLevel) blkEnt.getLevel();
        ItemStack stack = blkEnt.getInventory().getItem(slot);
        if (!stack.isEmpty() && stack.is(CCXItems.PERIPHERAL_CARD.get())) {
            functions.put("attachPeripheral", attachPeripheral);
            functions.put("detachPeripheral", detachPeripheral);
        }


        return functions;
    }
}
