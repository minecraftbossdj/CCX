package com.awesoft.ccx.block.rack;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import com.awesoft.ccx.registry.CCXBlockEntities;
import com.awesoft.ccx.registry.CCXItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RackBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty SERVER_SLOT = IntegerProperty.create("server_slot", 0, 8);

    public RackBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(SERVER_SLOT,0)
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
        builder.add(SERVER_SLOT);
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
        return new RackBlockEntity(pos, state);
    }

    public static int getSlotFromHit(BlockState state, BlockHitResult hit) {
        Vec3 hitVec = hit.getLocation().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ());
        float x = (float) hitVec.x;
        float y = (float) hitVec.y;
        float z = (float) hitVec.z;

        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        switch (facing) {
            case SOUTH -> { x = 1 - x; z = 1 - z; }
            case WEST -> { float t = x; x = z; z = 1 - t; }
            case EAST -> { float t = x; x = 1 - z; z = t; }
        }

        if (y > 0.875f || y < 0.125f) return -1;

        float pixelY = y * 16.0f;

        if (pixelY >= 2 && pixelY < 5) return 3;
        if (pixelY >= 5 && pixelY < 8) return 2;
        if (pixelY >= 8 && pixelY < 11) return 1;
        if (pixelY >= 11 && pixelY < 14) return 0;

        return -1;
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof RackBlockEntity rack)) return InteractionResult.PASS;


        int slot = getSlotFromHit(state,hit);
        if (slot == -1) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);
        ItemStack slotStack = rack.getInventory().getItem(slot);

        if (held.isEmpty()) {
            if (!slotStack.isEmpty()) {
                ItemStack toDrop = slotStack.copy();
                rack.disconnectServer(slot);
                rack.getInventory().removeItem(slot, 1);
                if (!player.addItem(toDrop)) player.drop(toDrop, false);

                rack.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        } else if (slotStack.isEmpty() && held.getItem() instanceof ServerPocketItem || slotStack.isEmpty() && held.is(CCXItems.SERVER_REMOTE.get())) {
            ItemStack insert = held.copy();
            insert.setCount(1);
            rack.getInventory().setItem(slot, insert);
            if (!player.isCreative()) held.shrink(1);
            rack.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
            return InteractionResult.SUCCESS;
        } else if (!slotStack.isEmpty() && held.is(CCXItems.REMOTE_TERMINAL.get())) {
            CCX.LOGGER.info("kill");
            if (slotStack.is(CCXItems.SERVER_REMOTE.get())) {
                CCX.LOGGER.info("hi!");
                CompoundTag tag = held.getOrCreateTag();
                CompoundTag rackPos = new CompoundTag();
                rackPos.putInt("x", rack.getBlockPos().getX());
                rackPos.putInt("y", rack.getBlockPos().getY());
                rackPos.putInt("z", rack.getBlockPos().getZ());

                tag.put("rackPos", rackPos);
                tag.putInt("slot", slot);
                player.displayClientMessage(Component.literal("Bound to Remote Server!"), true);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof RackBlockEntity rack) {
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
            if (level.getBlockEntity(pos) instanceof RackBlockEntity rack) {
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
        return createTickerHelper(type, CCXBlockEntities.RACK_ENTITY.get(), RackBlockEntity::tick);
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
