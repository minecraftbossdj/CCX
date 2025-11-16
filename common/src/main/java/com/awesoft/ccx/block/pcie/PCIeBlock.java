package com.awesoft.ccx.block.pcie;

import com.awesoft.ccx.item.rack.server.ServerPocketItem;
import com.awesoft.ccx.registry.CCXBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PCIeBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PCIeBlock(Properties pProperties) {
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
        return new PCIeBlockEntity(pos, state);
    }

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

        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            if (pixelX >= 2 && pixelX < 4) return 0;
            if (pixelX >= 5 && pixelX < 8) return 1;
            if (pixelX >= 10 && pixelX < 12) return 2;
            if (pixelX >= 13 && pixelX <= 16) return 3;
        } else {
            if (pixelX >= 2 && pixelX < 4) return 3;
            if (pixelX >= 5 && pixelX < 8) return 2;
            if (pixelX >= 10 && pixelX < 12) return 1;
            if (pixelX >= 13 && pixelX <= 16) return 0;
        }

        return -1;
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0 , 16, 8, 16);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Block.box(0, 0, 0 , 16, 8, 16);
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PCIeBlockEntity rack)) return InteractionResult.PASS;


        int slot = getSlotFromHit(state,hit);
        if (slot == -1) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);
        ItemStack slotStack = rack.getInventory().getItem(slot);

        TagKey<Item> CARDS = TagKey.create(Registries.ITEM, new ResourceLocation("ccx", "pcie_card"));

        if (held.isEmpty()) {
            if (!slotStack.isEmpty()) {
                ItemStack toDrop = slotStack.copy();
                rack.getInventory().removeItem(slot, 1);
                if (!player.addItem(toDrop)) player.drop(toDrop, false);

                rack.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        } else if (slotStack.isEmpty() && held.is(CARDS)) {
            ItemStack insert = held.copy();
            insert.setCount(1);
            rack.getInventory().setItem(slot, insert);
            if (!player.isCreative()) held.shrink(1);
            rack.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof PCIeBlockEntity rack) {
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


    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
