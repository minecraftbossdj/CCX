package com.awesoft.ccx.block.rack;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.item.rack.server.ServerBrain;
import com.awesoft.ccx.item.rack.server.ServerHolder;
import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import com.awesoft.ccx.lib.DirectionLib;
import com.awesoft.ccx.registry.CCXBlockEntities;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.platform.ComponentAccess;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RackBlockEntity extends BlockEntity implements Container {

    private int invalidSides = 0;
    private final ComponentAccess<IPeripheral> peripherals = PlatformHelper.get().createPeripheralAccess(this, (d) -> this.invalidSides |= 1 << d.ordinal());

    public Map<ComputerSide, IPeripheral> periphs;

    private boolean initialized = false;
    boolean suppressUpdates = false;

    public void onChange() {
        setChanged();
        if (level == null || level.isClientSide) return;
        if (suppressUpdates) return;

        updateComputers();
        turnOnComputers();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private final RackInventory inventory = new RackInventory(this,4);

    public RackBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(CCXBlockEntities.RACK_ENTITY.get(), pPos, pBlockState);
    }

    private void turnOnComputers() {
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof ServerPocketItem item) {
                ServerBrain brain = item.getOrCreateBrain(serverLevel, new ServerHolder.RackHolder(this, slot), stack);

                brain.computer().turnOn();
            }
        }

    }

    public void disconnectAllServers() {
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof ServerPocketItem item) {
                ServerBrain brain = item.getOrCreateBrain(serverLevel, new ServerHolder.RackHolder(this, slot), stack);
                ServerComputer comp = brain.computer();
                comp.shutdown();
                for (ComputerSide side : ComputerSide.values()) {
                    comp.setPeripheral(side,null);
                }
            }
        }
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void disconnectServer(int slot) {
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        ItemStack stack = inventory.getItem(slot);
        if (stack.isEmpty()) return;

        if (stack.getItem() instanceof ServerPocketItem item) {
            ServerBrain brain = item.getOrCreateBrain(serverLevel, new ServerHolder.RackHolder(this, slot), stack);
            ServerComputer comp = brain.computer();
            comp.shutdown();
            for (ComputerSide side : ComputerSide.values()) {
                comp.setPeripheral(side,null);
            }
        }
    }

    private void updateComputers() {
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (level.getServer() == null) return;

        Direction facing = getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof ServerPocketItem item) {
                ServerBrain brain = item.getOrCreateBrain(serverLevel, new ServerHolder.RackHolder(this, slot), stack);

                // loop through all six sides for this rack
                for (Direction dir : Direction.values()) {
                    IPeripheral periph = peripherals.get(dir);
                    ComputerSide side = DirectionLib.toComputerSide(dir, facing);

                    if (periph != null) {
                        brain.computer().setPeripheral(side, periph);
                    } else {
                        // clear any previously attached peripheral if no longer attached/alive
                        brain.computer().setPeripheral(side, null);
                    }
                }
                brain.computer().keepAlive();
            }
        }
    }



    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        this.level = level;
        if (level == null || level.isClientSide) return;

        if (!initialized && level instanceof ServerLevel serverLevel) {
            var server = serverLevel.getServer();
            if (server == null) return;
            initialized = true;
        }
    }


    public void neighborChanged(Direction dir, BlockPos neighborPos) {
        if (level == null || level.isClientSide) return;

        BlockEntity neighbor = level.getBlockEntity(neighborPos);
        if (neighbor == null) return;

        updateComputers();
    }

    /*
    public void neighborChanged(Direction dir, BlockPos pos) {
        CCX.LOGGER.info("received neighbor changed");
        assert this.level != null;
        var compSide = toComputerSide(dir,this.level.getBlockState(this.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING));
        BlockEntity blockEnt = this.level.getBlockEntity(pos);
        if (blockEnt == null) return;

        IPeripheral periph = peripherals.get(dir);

        for (int i = 0; i < 4; i++) {
            CCX.LOGGER.info("im spongebob");
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (this.level.isClientSide) return;
                if (stack.getItem() instanceof ServerPocketItem) {
                    ServerBrain brain = ((ServerPocketItem) stack.getItem()).getOrCreateBrain((ServerLevel) this.getLevel(), new ServerHolder.RackHolder(this, i), stack);

                    brain.computer().setPeripheral(compSide, periph);
                }
            }
        }

    }*/


    private boolean firstTickHasPast = false;

    public static void tick(Level level, BlockPos pos, BlockState state, RackBlockEntity blockEntity) {
        if (level.isClientSide) return;

        for (int i = 0; i < blockEntity.inventory.getContainerSize(); i++) {
            ItemStack stack = blockEntity.inventory.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof ServerPocketItem item) {
                ServerBrain brain = item.getOrCreateBrain((ServerLevel) level, new ServerHolder.RackHolder(blockEntity, i), stack);
                brain.computer().keepAlive();
            }
        }
        if (!blockEntity.firstTickHasPast) {
            blockEntity.firstTickHasPast = true;
            blockEntity.updateComputers();
            blockEntity.turnOnComputers();
        }
    }

    public RackInventory getInventory() {
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

