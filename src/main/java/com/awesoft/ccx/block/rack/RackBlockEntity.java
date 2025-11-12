package com.awesoft.ccx.block.rack;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.item.rack.server.ServerBrain;
import com.awesoft.ccx.item.rack.server.ServerHolder;
import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import com.awesoft.ccx.lib.DirectionLib;
import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx.registry.CCXItems;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.Capabilities;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.peripheral.modem.ModemPeripheral;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlockEntity;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemPeripheral;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.awesoft.ccx.lib.DirectionLib.toComputerSide;

public class RackBlockEntity extends BlockEntity {

    private int invalidSides = 0;
    private final ComponentAccess<IPeripheral> peripherals = PlatformHelper.get().createPeripheralAccess(this, (d) -> this.invalidSides |= 1 << d.ordinal());

    public Map<ComputerSide, IPeripheral> periphs;

    private final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                updateComputers();
                turnOnComputers();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(CCXItems.SERVER_ADVANCED.get());
        }
    };

    public RackBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(CCXBlockEntities.RACK_ENTITY.get(), pPos, pBlockState);
    }

    private void turnOnComputers() {
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
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

        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
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

        ItemStack stack = inventory.getStackInSlot(slot);
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

        Direction facing = getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof ServerPocketItem item) {
                ServerBrain brain = item.getOrCreateBrain(serverLevel, new ServerHolder.RackHolder(this, slot), stack);

                // Loop through all six sides for this rack
                for (Direction dir : Direction.values()) {
                    IPeripheral periph = peripherals.get(dir);
                    ComputerSide side = DirectionLib.toComputerSide(dir, facing);

                    if (periph != null) {
                        brain.computer().setPeripheral(side, periph);
                    } else {
                        // Clear any previously attached peripheral if now missing
                        brain.computer().setPeripheral(side, null);
                    }
                }
                brain.computer().keepAlive();
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            updateComputers();
            turnOnComputers();
        }
    }

    public void neighborChanged(Direction dir, BlockPos neighborPos) {
        if (level == null || level.isClientSide) return;
        CCX.LOGGER.info("Neighbor changed at {}", neighborPos);

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



    public static void tick(Level level, BlockPos pos, BlockState state, RackBlockEntity blockEntity) {
        if (level.isClientSide) return;

        for (int i = 0; i < blockEntity.inventory.getSlots(); i++) {
            ItemStack stack = blockEntity.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof ServerPocketItem item) {
                ServerBrain brain = item.getOrCreateBrain((ServerLevel) level, new ServerHolder.RackHolder(blockEntity, i), stack);
                brain.computer().keepAlive();
            }
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public void setItem(int slot, ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }

    public ItemStack getItem(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("Inventory", inventory.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(tag.getCompound("Inventory"));
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() == null) return;
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> inventory).cast();
        }
        return super.getCapability(cap, side);
    }
}
