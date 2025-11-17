package com.awesoft.ccx_drones.entity.drone;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DroneInventory implements Container {
    private final NonNullList<ItemStack> items;

    DroneEntity entity;

    public DroneInventory(int size, DroneEntity entity) {
        items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.entity = entity;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = items.get(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;

        int removed = Math.min(count, stack.getCount());
        ItemStack result = stack.copy();
        result.setCount(removed);

        stack.shrink(removed);
        if (stack.getCount() <= 0) items.set(slot, ItemStack.EMPTY);

        setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
        entity.syncInventoryToClient();
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}

