package com.awesoft.ccx.block.usb;

import com.awesoft.ccx.registry.CCXBlockEntities;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class USBBlockEntity extends BlockEntity implements Container {

    private boolean initialized = false;
    boolean suppressUpdates = false;

    public void onChange() {
        setChanged();
        if (level == null || level.isClientSide) return;
        if (suppressUpdates) return;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

    }

    private final USBInventory inventory = new USBInventory(this,4);

    public USBBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(CCXBlockEntities.USB_PORT_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        this.level = level;
        if (level == null || level.isClientSide) return;

        if (!initialized && level instanceof ServerLevel serverLevel) {
            if (serverLevel.getServer() == null) return;
            initialized = true;
        }
    }

    public IPeripheral createPeripheral() {
        return new USBPeripheral(this);
    }

    public USBInventory getInventory() {
        return inventory;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }

    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return inventory.removeItem(i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return inventory.removeItemNoUpdate(i);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        suppressUpdates = true;
        try {
            if (tag.contains("Inventory")) {
                inventory.load(tag.getCompound("Inventory"));
            }
        } finally {
            suppressUpdates = false;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }
}

