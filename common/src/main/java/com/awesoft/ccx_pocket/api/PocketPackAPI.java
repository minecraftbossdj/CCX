package com.awesoft.ccx_pocket.api;

import com.awesoft.ccx_pocket.CCXPocket;
import com.awesoft.ccx_pocket.item.PocketPack.PocketPackInventory;
import com.awesoft.ccx_pocket.item.PocketPack.PocketPackItem;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class PocketPackAPI implements ILuaAPI {
    private final IComputerSystem computer;
    private final IPocketAccess pocket;

    public PocketPackAPI(IComputerSystem computer, IPocketAccess pocket) {
        this.computer = computer;
        this.pocket = pocket;
    }

    public final Level getLevel() {
        return computer.getLevel();
    }

    public final ServerPlayer getPlayer() {
        if (pocket.getEntity() instanceof ServerPlayer player) {
            return player;
        } else {
            return null;
        }
    }

    public final Boolean isPlayerAlive() {
        return getPlayer() != null;
    }

    public final UUID getOwnerUUID() {
        if (getPlayer() != null) {
            return getPlayer().getUUID();
        }
        return null;
    }

    public final ItemStack getPocketItem() {
        if (!isPlayerAlive()) return null;

        var main = getPlayer().getMainHandItem();
        var off = getPlayer().getOffhandItem();
        var chest = getPlayer().getItemBySlot(EquipmentSlot.CHEST);
        if (!main.isEmpty() && main.getItem() instanceof PocketPackItem) return main;
        if (!off.isEmpty() && off.getItem() instanceof PocketPackItem) return off;
        if (!chest.isEmpty() && chest.getItem() instanceof PocketPackItem) return chest;
        return null;
    }

    @Override
    public String[] getNames() {
        return new String[]{"pack"};
    }

    public MethodResult moveItemTo(Container from, Container to, int fromSlot, int limit, int toSlot) {
        ItemStack fromItem = from.getItem(Math.max(0,fromSlot - 1));
        ItemStack toItem = to.getItem(Math.max(0,toSlot - 1));

        if (fromItem.isEmpty()) {
            return MethodResult.of(false, "From slot is empty!");
        }

        if (toItem.isEmpty()) {
            if (fromItem.getCount() < limit) {
                return MethodResult.of(false, "From slot has too few items!");
            }

            ItemStack moved = fromItem.copy();
            moved.setCount(limit);

            fromItem.shrink(limit);
            from.setItem(Math.max(0,fromSlot - 1), fromItem);
            to.setItem(Math.max(0,toSlot - 1), moved);

            return MethodResult.of(true, limit);
        }

        if (toItem.getItem() != fromItem.getItem()) {
            return MethodResult.of(false, "To slot doesn't contain the same item!");
        }

        if (!Objects.equals(toItem.getTag(), fromItem.getTag())) {
            return MethodResult.of(false, "Both items' NBT do not match.");
        }

        int transferable = Math.min(limit, fromItem.getCount());
        int space = toItem.getMaxStackSize() - toItem.getCount();
        int movedAmount = Math.min(transferable, space);

        fromItem.shrink(movedAmount);
        toItem.grow(movedAmount);

        from.setItem(Math.max(0,fromSlot - 1), fromItem);
        to.setItem(Math.max(0,toSlot - 1), toItem);

        return MethodResult.of(true, movedAmount);
    }

    public final Inventory getInventory() {
        if (!isPlayerAlive()) return null;
        return getPlayer().getInventory();
    }

    public final SimpleContainer getPack() {
        ItemStack item = getPocketItem();
        if (item.getItem() instanceof PocketPackItem pack) {
            return pack.getInventory(item);
        }
        return null;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult list(String invString) {
        var inv = getInventory();
        var pack = getPack();
        if (inv == null || pack == null) {return MethodResult.of(false,"Player doesnt exist!");}

        Container contain1 = null;

        if (invString.equalsIgnoreCase("inv")) {contain1 = inv;}

        if (invString.equalsIgnoreCase("pack")) {contain1 = pack;}

        if (contain1 == null) {return MethodResult.of(false,"Invalid Inventory type! Please use \"inv\" or \"pack\"!");}

        Map<Integer, Map<String, ?>> result = new HashMap<>();
        var size = contain1.getContainerSize();
        for (var i = 0; i < size; i++) {
            var stack = contain1.getItem(i);
            if (!stack.isEmpty()) result.put(i + 1, VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack));
        }
        if (contain1 instanceof PocketPackInventory packInv) {
            if (getPocketItem().getItem() instanceof PocketPackItem item) {
                item.saveInventory(getPocketItem(), packInv);
            }
        }
        return MethodResult.of(result);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult moveItems(String fromInv, String toInv, int fromSlot, int limit, int toSlot) {
        var inv = getInventory();
        var pack = getPack();
        if (inv == null || pack == null) {return MethodResult.of(false,"Player doesnt exist!");}

        Container contain1 = null;
        Container contain2 = null;

        if (fromInv.equalsIgnoreCase("inv")) {contain1 = inv;}
        if (toInv.equalsIgnoreCase("inv")) {contain2 = inv;}

        if (fromInv.equalsIgnoreCase("pack")) {contain1 = pack;}
        if (toInv.equalsIgnoreCase("pack")) {contain2 = pack;}

        if (contain1 == null || contain2 == null) {return MethodResult.of(false,"Invalid Inventories! Please use \"inv\" or \"pack\"!");}

        if (contain1.getItem(fromSlot-1).getItem() instanceof PocketPackItem) return MethodResult.of(false, "Cannot put Pocket Pack into another Pocket Pack!");

        MethodResult result = moveItemTo(contain1,contain2,fromSlot,limit,toSlot);

        if (contain1 instanceof PocketPackInventory packInv) {
            if (getPocketItem().getItem() instanceof PocketPackItem item) {
                item.saveInventory(getPocketItem(), packInv);
            }
        } else if (contain2 instanceof PocketPackInventory packInv) {
            if (getPocketItem().getItem() instanceof PocketPackItem item) {
                item.saveInventory(getPocketItem(), packInv);
            }
        }

        return MethodResult.of(result);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItemDetail(int slotNumFake, String targetInv) {
        var inv = getInventory();
        var pack = getPack();

        if (inv == null || pack == null) {return MethodResult.of(false,"Player doesnt exist!");}

        Container contain1 = null;

        if (targetInv.equalsIgnoreCase("inv")) {contain1 = inv;}

        if (targetInv.equalsIgnoreCase("pack")) {contain1 = pack;}

        if (contain1 == null) {return MethodResult.of(false,"Invalid Inventory type! Please use \"inv\" or \"pack\"!");}

        int slotNum = Math.max(0,Math.min(contain1.getContainerSize(),slotNumFake-1));

        return MethodResult.of(VanillaDetailRegistries.ITEM_STACK.getBasicDetails(contain1.getItem(slotNum)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItemLimit(int slotNumFake, String targetInv) {
        var inv = getInventory();
        var pack = getPack();

        if (inv == null || pack == null) {return MethodResult.of(false,"Player doesnt exist!");}

        Container contain1 = null;

        if (targetInv.equalsIgnoreCase("inv")) {contain1 = inv;}

        if (targetInv.equalsIgnoreCase("pack")) {contain1 = pack;}

        if (contain1 == null) {return MethodResult.of(false,"Invalid Inventory type! Please use \"inv\" or \"pack\"!");}

        int slotNum = Math.max(0,Math.min(contain1.getContainerSize(),slotNumFake-1));

        return MethodResult.of(contain1.getItem(slotNum).getMaxStackSize());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult size(String targetInv) {
        var inv = getInventory();
        var pack = getPack();

        if (inv == null || pack == null) {return MethodResult.of(false, "Player doesnt exist!");}

        Container contain1 = null;

        if (targetInv.equalsIgnoreCase("inv")) {contain1 = inv;}

        if (targetInv.equalsIgnoreCase("pack")) {contain1 = pack;}

        if (contain1 == null) {return MethodResult.of(false,"Invalid Inventory type! Please use \"inv\" or \"pack\"!");}

        return MethodResult.of(contain1.getContainerSize());
    }

}
