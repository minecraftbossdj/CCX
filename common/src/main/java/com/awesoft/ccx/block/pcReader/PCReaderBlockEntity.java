package com.awesoft.ccx.block.pcReader;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.item.rack.server.ServerBrain;
import com.awesoft.ccx.item.rack.server.ServerHolder;
import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import com.awesoft.ccx.lib.DirectionLib;
import com.awesoft.ccx.registry.CCXBlockEntities;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.platform.ComponentAccess;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
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
import java.util.UUID;

public class PCReaderBlockEntity extends BlockEntity implements Container {

    private int invalidSides = 0;
    private final ComponentAccess<IPeripheral> peripherals = PlatformHelper.get().createPeripheralAccess(this, (d) -> this.invalidSides |= 1 << d.ordinal());

    public Map<ComputerSide, IPeripheral> periphs;

    public void onChange() {
        setChanged();
        if (level != null && !level.isClientSide) {
            updateComputers();
            turnOnComputers();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private final PCReaderInventory inventory = new PCReaderInventory(this,4);

    public PCReaderBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(CCXBlockEntities.PC_READER_ENTITY.get(), pPos, pBlockState);
    }

    private static int getSessionID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains("SessionId") ? nbt.getInt("SessionId") : -1;
    }

    @javax.annotation.Nullable
    public static UUID getInstanceID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.hasUUID("InstanceId") ? nbt.getUUID("InstanceId") : null;
    }

    @javax.annotation.Nullable
    public static ServerComputer getServerComputer(ServerComputerRegistry registry, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        try {
            int sessionId = tag.getInt("SessionId");
            UUID instanceId = tag.getUUID("InstanceId");
            if (instanceId != null) {
                return registry.get(sessionId, instanceId);
            }
        } catch (RuntimeException ignored) {}
        return null;
    }

    @javax.annotation.Nullable
    public static ServerComputer getServerComputer(MinecraftServer server, ItemStack stack) {
        if (server != null) {
            if (ServerContext.get(server) == null) return null;
            return getServerComputer(ServerContext.get(server).registry(), stack);
        } else {
            return null;
        }
    }

    private void turnOnComputers() {
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        MinecraftServer server = serverLevel.getServer();

        ItemStack stack = inventory.getItem(0);
        if (stack.isEmpty()) return;

        ServerComputer comp = getServerComputer(server, stack);
        if (comp != null) {
            comp.turnOn();
        }

    }

    public void disconnectAllServers() {
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        MinecraftServer server = serverLevel.getServer();

        ItemStack stack = inventory.getItem(0);
        if (stack.isEmpty()) return;

        ServerComputer comp = getServerComputer(server, stack);
        if (comp != null) {
            comp.shutdown();
            for (ComputerSide side : ComputerSide.values()) {
                comp.setPeripheral(side,null);
            }
        }

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void disconnectServer(int slot) {
        if (level == null || level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        MinecraftServer server = serverLevel.getServer();

        ItemStack stack = inventory.getItem(0);
        if (stack.isEmpty()) return;

        ServerComputer comp = getServerComputer(server, stack);
        if (comp != null) {
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

        MinecraftServer server = serverLevel.getServer();

        ItemStack stack = inventory.getItem(0);
        if (stack.isEmpty()) return;

        ServerComputer comp = getServerComputer(server, stack);
        if (comp != null) {
            for (Direction dir : Direction.values()) {
                IPeripheral periph = peripherals.get(dir);
                ComputerSide side = DirectionLib.toComputerSide(dir, facing);
                comp.setPeripheral(side, periph);
            }
            comp.keepAlive();
        }


    }

    private boolean initialized = false;

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        this.level = level;
        if (level != null && !level.isClientSide && !initialized) {
            initialized = true;
            updateComputers();
            turnOnComputers();
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



    public static void tick(Level level, BlockPos pos, BlockState state, PCReaderBlockEntity blockEntity) {
        if (level.isClientSide) return;

        if (level instanceof ServerLevel serverLevel) {
            MinecraftServer server = serverLevel.getServer();

            ItemStack stack = blockEntity.inventory.getItem(0);
            if (stack.isEmpty()) return;

            try {
                ServerComputer comp = getServerComputer(server, stack);
                if (comp != null) {
                    comp.keepAlive();
                }
            } catch (RuntimeException ignored) {}
        }
    }

    public PCReaderInventory getInventory() {
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
        if (tag.contains("Inventory")) {
            inventory.load(tag.getCompound("Inventory"));
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

