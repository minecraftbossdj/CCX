package com.awesoft.ccx.block.pcReader;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.registry.CCXItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class PCReaderInventory extends SimpleContainer {
    private final PCReaderBlockEntity parent;

    public PCReaderInventory(PCReaderBlockEntity parent, int size) {
        super(size);
        this.parent = parent;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        parent.setChanged();
        if (parent.getLevel() != null && !parent.getLevel().isClientSide()) {
            parent.getLevel().sendBlockUpdated(
                    parent.getBlockPos(),
                    parent.getBlockState(),
                    parent.getBlockState(),
                    3
            );
        }
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


    @Override
    public void setItem(int slot, ItemStack stack) {
        if (parent.getLevel() == null) {
            CCX.LOGGER.info("how what");
            return;
        }
        super.setItem(slot, stack);
        parent.onChange();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = super.removeItem(slot, amount);
        parent.onChange();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = super.removeItemNoUpdate(slot);
        parent.onChange();
        return result;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return true;
    }
}
