package com.awesoft.ccx_pocket.item.PocketPack;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.registry.CCXItems;
import com.awesoft.ccx_pocket.item.PocketPack.PocketPackItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class PocketPackInventory extends SimpleContainer {
    private final PocketPackItem parent;

    public PocketPackInventory(PocketPackItem parent, int size) {
        super(size);
        this.parent = parent;
    }

    private NonNullList<ItemStack> getItems() {
        var list = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < getContainerSize(); i++) {
            list.set(i, getItem(i));
        }
        return list;
    }

    public CompoundTag save(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, getItems());
        return tag;
    }

    public void load(CompoundTag tag) {
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);

        for (int i = 0; i < getContainerSize(); i++) {
            super.setItem(i, items.get(i));
        }
    }
}
