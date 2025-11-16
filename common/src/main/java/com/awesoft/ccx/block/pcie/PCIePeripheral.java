package com.awesoft.ccx.block.pcie;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.lib.periph.PeripheralAccessProvider;
import com.awesoft.ccx.luaFunctions.cards.InventoryCardFunctions;
import com.awesoft.ccx.luaFunctions.cards.PeripheralCardFunctions;
import com.awesoft.ccx.registry.CCXItems;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.ExecutableElement;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PCIePeripheral implements IPeripheral {

    private List<IComputerAccess> computers = new ArrayList<>();

    private final PCIeBlockEntity blkEntity;

    public PCIePeripheral(PCIeBlockEntity blkEntity) {
        this.blkEntity = blkEntity;
    }


    @Override
    public String getType() {
        return "pcie";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }

    @Override
    public void attach(IComputerAccess computer) {
        IPeripheral.super.attach(computer);
        computers.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        IPeripheral.super.detach(computer);
        computers.remove(computer);
    }

    @LuaFunction(mainThread = true)
    public Map<String, Object> getMethods() {
        if (blkEntity.getLevel() == null || blkEntity.getLevel().isClientSide) return null;
        Map<String, Object> functions = new HashMap<>();
        for (int i = 0; i < blkEntity.getContainerSize(); i++) {
            ItemStack item = blkEntity.getInventory().getItem(i);
            if (!item.isEmpty()) {
                if (item.is(CCXItems.PERIPHERAL_CARD.get())) {
                    if (item.getTag() == null) return null;
                    CompoundTag tag = item.getTag().getCompound("periphPos");

                    BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));

                    PeripheralCardFunctions funcs = new PeripheralCardFunctions(blkEntity, computers, i, blkEntity.getLevel().getBlockEntity(pos));
                    functions.put("card_" + (i+1), funcs.getFunctions());
                } else if (item.is(CCXItems.INVENTORY_CARD.get())) {
                    if (item.getTag() == null) return null;
                    CompoundTag tag = item.getTag().getCompound("invPos");

                    BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));

                    Container ent = null;

                    if (blkEntity.getLevel().getBlockEntity(pos) instanceof Container inv) {
                        ent = inv;
                    }
                    InventoryCardFunctions funcs = new InventoryCardFunctions(blkEntity, i, ent);
                    functions.put("card_" + (i + 1), funcs.getFunctions());
                }
            }
        }
        return functions;
    }
}
