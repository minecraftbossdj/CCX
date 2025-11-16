package com.awesoft.ccx.block.pcReader;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import com.awesoft.ccx.registry.CCXBlockEntities;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PCReaderBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PCReaderBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() == null) return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PCReaderBlockEntity(pos, state);
    }

    /*
    public static int getSlotFromHit(BlockState state, BlockHitResult hit) {
        Vec3 hitVec = hit.getLocation().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ());
        float x = (float) hitVec.x;
        float y = (float) hitVec.y;
        float z = (float) hitVec.z;

        if (y < 0.4375f || y > 0.5625f) return -1;

        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        switch (facing) {
            case SOUTH -> { x = 1 - x; z = 1 - z; }
            case WEST  -> { float t = x; x = z; z = 1 - t; }
            case EAST  -> { float t = x; x = 1 - z; z = t; }
        }

        x = 1.0f - x;

        float pixelX = x * 16.0f;

        if (pixelX >= 2 && pixelX < 4) return 3;
        if (pixelX >= 5 && pixelX < 8) return 2;
        if (pixelX >= 10 && pixelX < 12) return 1;
        if (pixelX >= 13 && pixelX <= 16) return 0;

        return -1;
    }*/

    public static int getSlotFromHit(BlockState state, BlockHitResult hit) {
        if (hit.getDirection() != Direction.UP) return -1;

        Vec3 hitVec = hit.getLocation()
                .subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ());

        float x = (float) hitVec.x;
        float z = (float) hitVec.z;


        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        switch (facing) {
            case SOUTH -> { x = 1 - x; z = 1 - z; }
            case WEST  -> { float t = x; x = z; z = 1 - t; }
            case EAST  -> { float t = x; x = 1 - z; z = t; }
        }

        x = 1.0f - x;

        float pixelX = x * 16.0f;
        float pixelY = (float)(hitVec.y * 16.0f);
        CCX.LOGGER.info(pixelX+" and "+pixelY);

        if (pixelY < 16 || pixelY > 16) return -1;


        if (pixelX >= 4 && pixelX < 11) return 0;



        return -1;
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
            return getServerComputer(ServerContext.get(server).registry(), stack);
        } else {
            return null;
        }
    }

    private static void openImpl(Player player, ItemStack stack, boolean isTypingOnly, ServerComputer computer) {
        PlatformHelper.get().openMenu(player, stack.getHoverName(), (id, inventory, entity) -> new ComputerMenuWithoutInventory((MenuType) ModRegistry.Menus.COMPUTER.get(), id, inventory, (p) -> true, computer), new ComputerContainerData(computer, stack));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PCReaderBlockEntity pcreader)) return InteractionResult.PASS;


        int slot = getSlotFromHit(state,hit);

        CCX.LOGGER.info(slot);

        ItemStack held = player.getItemInHand(hand);
        ItemStack slotStack = pcreader.getInventory().getItem(0);

        //if (slotStack.getTag() == null) return InteractionResult.FAIL;

        if (slot == 0) {
            if (held.isEmpty()) {
                if (!slotStack.isEmpty()) {
                    ItemStack toDrop = slotStack.copy();
                    pcreader.disconnectServer(slot);
                    pcreader.getInventory().removeItem(slot, 1);
                    if (!player.addItem(toDrop)) player.drop(toDrop, false);

                    pcreader.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
                    return InteractionResult.SUCCESS;
                }
            } else if (slotStack.isEmpty()) {
                ItemStack insert = held.copy();
                insert.setCount(1);
                pcreader.getInventory().setItem(slot, insert);
                if (!player.isCreative()) held.shrink(1);
                pcreader.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        } else {
            if (player.getServer() == null) {
                CCX.LOGGER.info("youll never see this message! isnt that interesting?");
                return InteractionResult.CONSUME;
            }

            ItemStack slotStackReal = pcreader.getInventory().getItem(0);
            if (slotStackReal.isEmpty() || slotStackReal.getCount() == 0) return InteractionResult.FAIL;
            CompoundTag tag = slotStackReal.getTag();
            CCX.LOGGER.info(tag);
            ServerComputerRegistry registry = ServerContext.get(player.getServer()).registry();
            ServerComputer comp = getServerComputer(registry, slotStackReal);
            if (comp == null) {
                player.displayClientMessage(Component.literal("That item isn't a computer type item!"),true);
                return InteractionResult.FAIL;
            }
            openImpl(player, slotStackReal, false, comp);


        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof PCReaderBlockEntity rack) {
                Direction side = null;
                for (Direction d : Direction.values()) {
                    if (pos.relative(d).equals(fromPos)) {
                        side = d;
                        break;
                    }
                }
                if (side != null) {
                    rack.neighborChanged(side, fromPos);
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof PCReaderBlockEntity rack) {
                rack.disconnectAllServers();

                for (int i = 0; i < 4; i++) {
                    ItemStack stack = rack.getItem(i);
                    if (!stack.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }

                level.updateNeighbourForOutputSignal(pos, this);
                level.removeBlockEntity(pos);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CCXBlockEntities.PC_READER_ENTITY.get(), PCReaderBlockEntity::tick);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
