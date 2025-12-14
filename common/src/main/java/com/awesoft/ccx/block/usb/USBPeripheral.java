package com.awesoft.ccx.block.usb;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.luaFunctions.cards.InventoryCardFunctions;
import com.awesoft.ccx.luaFunctions.cards.PeripheralCardFunctions;
import com.awesoft.ccx.luaFunctions.usb.WirelessUSBFunctions;
import com.awesoft.ccx.misc.WiFiNetwork;
import com.awesoft.ccx.registry.CCXItems;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class USBPeripheral implements IPeripheral {

    private List<IComputerAccess> computers = new ArrayList<>();

    private final USBBlockEntity blkEntity;

    public USBPeripheral(USBBlockEntity blkEntity) {
        this.blkEntity = blkEntity;
    }


    @Override
    public String getType() {
        return "usb_port";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }

    @Override
    public void attach(IComputerAccess computer) {
        IPeripheral.super.attach(computer);
        computers.add(computer);
        if (blkEntity.getInventory().getItem(0).is(CCXItems.WIRELESS_USB.get())) {
            ItemStack usb = blkEntity.getInventory().getItem(0);
            if (usb.getOrCreateTag().get("channel") == null) usb.getOrCreateTag().putInt("channel", 1);
            int channel = usb.getOrCreateTag().getInt("channel");
            WiFiNetwork.open(channel, computer, blkEntity);
        }
    }

    @Override
    public void detach(IComputerAccess computer) {
        IPeripheral.super.detach(computer);
        computers.remove(computer);
        if (blkEntity.getInventory().getItem(0).is(CCXItems.WIRELESS_USB.get())) {
            WiFiNetwork.close(computer);
        }
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getMethods() {
        if (blkEntity.getLevel() == null || blkEntity.getLevel().isClientSide) return null;
        Map<String, Object> functions = new HashMap<>();
        ItemStack item = blkEntity.getInventory().getItem(0);
        if (!item.isEmpty()) {
            if (item.is(CCXItems.WIRELESS_USB.get())) {
                WirelessUSBFunctions funcs = new WirelessUSBFunctions(blkEntity, computers);
                functions.put("usb", funcs.getFunctions());
            }
        }
        return functions;
    }
}
