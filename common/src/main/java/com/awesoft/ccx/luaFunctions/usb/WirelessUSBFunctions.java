package com.awesoft.ccx.luaFunctions.usb;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.awesoft.ccx.block.usb.USBBlock;
import com.awesoft.ccx.block.usb.USBBlockEntity;
import com.awesoft.ccx.lib.periph.PeripheralAccessProvider;
import com.awesoft.ccx.misc.WiFiNetwork;
import com.awesoft.ccx.registry.CCXItems;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WirelessUSBFunctions {
    public Map<String, Object> functions = new HashMap<>();

    private USBBlockEntity blkEnt;
    private List<IComputerAccess> computers = new ArrayList<>();
    private ServerLevel level;
    private BlockEntity periphBlockEntity;

    public WirelessUSBFunctions(USBBlockEntity entFake, List<IComputerAccess> comps) {
        this.blkEnt = entFake;
        this.computers = comps;
    }

    private boolean checkItem() {
        return blkEnt.getInventory().getItem(0).getItem().equals(CCXItems.WIRELESS_USB.get());
    }

    public ILuaFunction setChannel = args -> {
        if (!checkItem()) return MethodResult.of(false, "USB not inserted!");

        int channel = args.getInt(0);

        if (channel > 65535) channel = 65535;
        if (channel <= 0) channel = 1;

        blkEnt.getInventory().getItem(0).getOrCreateTag().putInt("channel", channel);

        return MethodResult.of(true, channel);
    };

    public ILuaFunction getChannel = args -> {
        if (!checkItem()) return MethodResult.of(false, "USB not inserted!");

        if (blkEnt.getInventory().getItem(0).getOrCreateTag().get("channel") == null) blkEnt.getInventory().getItem(0).getOrCreateTag().putInt("channel", 1);
        int channel = blkEnt.getInventory().getItem(0).getOrCreateTag().getInt("channel");



        return MethodResult.of(true, channel);
    };


    public ILuaFunction broadcast = args -> {
        if (!checkItem()) return MethodResult.of(false, "USB not inserted!");

        String message = args.getString(0);
        int channel = args.getInt(1);
        WiFiNetwork.broadcast(message,channel, computers);
        return MethodResult.of(true, "Message sent!");
    };

    public Map<String, Object> getFunctions() {
        if (blkEnt.getLevel() == null || blkEnt.getLevel().isClientSide) return null;
        ItemStack stack = blkEnt.getInventory().getItem(0);
        if (!stack.isEmpty() && stack.is(CCXItems.WIRELESS_USB.get())) {
            functions.put("setChannel", setChannel);
            functions.put("getChannel", getChannel);
            functions.put("broadcast", broadcast);
        }


        return functions;
    }
}
