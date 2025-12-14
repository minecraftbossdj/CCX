package com.awesoft.ccx.block.usb;

import com.awesoft.ccx.misc.WiFiNetwork;
import com.awesoft.ccx.registry.CCXItems;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class USBBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public USBBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(BlockStateProperties.FACING, Direction.NORTH)
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
        /*
        if (context.getPlayer() == null) return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());*/
        Direction face = context.getClickedFace();
        return this.defaultBlockState().setValue(FACING, face.getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new USBBlockEntity(pos, state);
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var facing = state.getValue(FACING);
        return switch (facing) {
            case DOWN -> Block.box(2, 0, 2, 14, 3, 14);
            case UP -> Block.box(2, 13, 2, 14, 16, 14);
            case NORTH -> Block.box(2, 2, 0, 14, 14, 3);
            case SOUTH -> Block.box(2, 2, 13, 14, 14, 16);
            case WEST -> Block.box(0, 2, 2, 3, 14, 14);
            case EAST -> Block.box(13, 2, 2, 16, 14, 14);
        };

    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        var facing = state.getValue(FACING);
        if (facing == Direction.NORTH) return Block.box(2, 2, 0, 14, 14, 3);
        if (facing == Direction.EAST) return Block.box(13, 2, 2, 16, 14, 14);
        if (facing == Direction.WEST) return Block.box(0, 2, 2, 3, 14, 14);
        if (facing == Direction.SOUTH) return Block.box(2, 2, 13, 14, 14, 16);

        return Block.box(2, 2, 0, 14, 14, 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing);
        BlockState support = level.getBlockState(supportPos);

        return support.isFaceSturdy(level, supportPos, facing);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof USBBlockEntity usb)) return InteractionResult.PASS;

        int slot = 0;

        ItemStack held = player.getItemInHand(hand);
        ItemStack slotStack = usb.getInventory().getItem(slot);

        TagKey<Item> USBS = TagKey.create(Registries.ITEM, new ResourceLocation("ccx", "usb"));

        if (held.isEmpty()) {
            if (!slotStack.isEmpty()) {
                ItemStack toDrop = slotStack.copy();
                if (toDrop.is(CCXItems.WIRELESS_USB.get())) {
                    WiFiNetwork.closeAll(usb);
                }
                usb.getInventory().removeItem(slot, 1);
                if (!player.addItem(toDrop)) player.drop(toDrop, false);

                usb.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        } else if (slotStack.isEmpty() && held.is(USBS)) {
            ItemStack insert = held.copy();
            insert.setCount(1);
            usb.getInventory().setItem(slot, insert);
            if (!player.isCreative()) held.shrink(1);
            usb.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof USBBlockEntity usb) {
                ItemStack stack = usb.getItem(0);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    if (stack.is(CCXItems.WIRELESS_USB.get())) {
                        WiFiNetwork.closeAll(usb);
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
