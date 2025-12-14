package com.awesoft.ccx.luaFunctions.cards;

import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.awesoft.ccx.lib.LuaConverter;
import com.awesoft.ccx.registry.CCXItems;
import com.mojang.brigadier.ParseResults;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CommandCardFunctions {

    public Map<String, Object> functions = new HashMap<>();

    private PCIeBlockEntity pcieBlock;
    private int cardSlot;

    public CommandCardFunctions(PCIeBlockEntity pcieBlock, int slot) {
        this.pcieBlock = pcieBlock;
        this.cardSlot = slot;
    }



    public ILuaFunction runCommand = args -> {
        if (pcieBlock.getLevel() == null || pcieBlock.getLevel().getServer() == null || pcieBlock.getLevel().isClientSide) return null;
        var source = pcieBlock.getLevel().getServer().createCommandSourceStack();
        pcieBlock.getLevel().getServer().getCommands().performPrefixedCommand(pcieBlock.getLevel().getServer().createCommandSourceStack(), args.getString(0));
        return MethodResult.of(true);
    };

    public ILuaFunction getPlayerPos = args -> {
        if (pcieBlock.getLevel() == null || pcieBlock.getLevel().getServer() == null || pcieBlock.getLevel().isClientSide) return null;
        var plr = pcieBlock.getLevel().getServer().getPlayerList().getPlayerByName(args.getString(0));
        if (plr == null) return MethodResult.of(false, "Player is not online or invalid username!");
        Map<String, Object> info = new HashMap<>();
        Map<String, Object> pos = new HashMap<>();

        pos.put("x", plr.getX());
        pos.put("y", plr.getY());
        pos.put("z", plr.getZ());
        info.put("pos",pos);
        return MethodResult.of(true, info);
    };


    public Map<String, Object> getFunctions() {
        if (pcieBlock.getLevel() == null || pcieBlock.getLevel().isClientSide) return null;
        ServerLevel level = (ServerLevel) pcieBlock.getLevel();
        ItemStack stack = pcieBlock.getInventory().getItem(cardSlot);
        if (!stack.isEmpty() && stack.is(CCXItems.INVENTORY_CARD.get())) {
            functions.put("runCommand", runCommand);
            functions.put("getPlayerPos", getPlayerPos);
        }


        return functions;
    }
}
