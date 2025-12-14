package com.awesoft.ccx.luaFunctions.cards;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.awesoft.ccx.lib.LuaConverter;
import com.awesoft.ccx.lib.periph.PeripheralAccessProvider;
import com.awesoft.ccx.registry.CCXItems;
import dan200.computercraft.api.client.turtle.RegisterTurtleUpgradeModeller;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryCardFunctions {

    public Map<String, Object> functions = new HashMap<>();

    private PCIeBlockEntity pcieBlock;
    private int cardSlot;
    private Container inv;

    private boolean checkItem() {
        return pcieBlock.getInventory().getItem(0).getItem().equals(CCXItems.INVENTORY_CARD.get());
    }

    public InventoryCardFunctions(PCIeBlockEntity pcieBlock, int slot, Container inv) {
        this.pcieBlock = pcieBlock;
        this.cardSlot = slot;
        this.inv = inv;
    }


    public ILuaFunction size = args -> {
        if (!checkItem()) return MethodResult.of(false, "Card not inserted!");

        if (inv == null) {
            return MethodResult.of(false, "Block is NOT a inventory!");
        }
        return MethodResult.of(inv.getContainerSize());
    };

    public ILuaFunction list = args -> {
        if (!checkItem()) return MethodResult.of(false, "Card not inserted!");

        if (inv == null) {
            return MethodResult.of(false, "Block is NOT a inventory!");
        }
        Map<Integer, Object> itemList = new HashMap<>();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            itemList.put(i+1, LuaConverter.itemToLua(i,inv));
        }
        return MethodResult.of(itemList);
    };


    public ILuaFunction getItemDetail = args -> {
        if (!checkItem()) return MethodResult.of(false, "Card not inserted!");

        if (inv == null) {
            return MethodResult.of(false, "Block is NOT a inventory!");
        }
        int slotFunc = args.getInt(0);
        return MethodResult.of(LuaConverter.itemToLua(slotFunc-1, inv));
    };

    public ILuaFunction getItemLimit = args -> {
        if (!checkItem()) return MethodResult.of(false, "Card not inserted!");

        if (inv == null) {
            return MethodResult.of(false, "Block is NOT a inventory!");
        }
        int slotFunc = args.getInt(0);
        return MethodResult.of(inv.getItem(slotFunc-1).getMaxStackSize());
    };


    public Map<String, Object> getFunctions() {
        if (pcieBlock.getLevel() == null || pcieBlock.getLevel().isClientSide) return null;
        ServerLevel level = (ServerLevel) pcieBlock.getLevel();
        ItemStack stack = pcieBlock.getInventory().getItem(cardSlot);
        if (!stack.isEmpty() && stack.is(CCXItems.INVENTORY_CARD.get())) {
            functions.put("size", size);
            functions.put("list", list);
            functions.put("getItemDetail", getItemDetail);
            functions.put("getItemLimit", getItemLimit);
        }


        return functions;
    }
}
