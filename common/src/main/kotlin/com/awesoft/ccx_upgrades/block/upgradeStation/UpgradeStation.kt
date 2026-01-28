package com.awesoft.ccx_upgrades.block.upgradeStation

import com.awesoft.ccx.block.pcie.PCIeBlock
import com.awesoft.ccx.block.rack.RackBlock
import com.awesoft.ccx.block.rack.RackBlockEntity
import com.awesoft.ccx.item.rack.server.ServerPocketItem
import com.awesoft.ccx.registry.CCXItems
import com.awesoft.ccx_upgrades.registry.CCXUBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class UpgradeStation(properties: Properties) : BaseEntityBlock(properties) {
    val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return UpgradeStationEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getOcclusionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos
    ): VoxelShape {
        return Shapes.empty()
    }

    @Deprecated("Deprecated in Java")
    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        if (level.getBlockEntity(blockPos) != null) {
            val BE = level.getBlockEntity(blockPos) as UpgradeStationEntity
            BE.onRemove()
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(PCIeBlock.FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        if (context.player == null) return this.defaultBlockState()
            .setValue(PCIeBlock.FACING, context.getHorizontalDirection().getOpposite())

        return this.defaultBlockState()
            .setValue(PCIeBlock.FACING, context.getHorizontalDirection().getOpposite())
    }

    override fun getShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return getRealShape()
    }

    override fun getCollisionShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return getRealShape()
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS

        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity !is UpgradeStationEntity) return InteractionResult.PASS

        blockEntity.onUse(state, level, pos, player, hand, hit)

        return InteractionResult.SUCCESS
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T?>
    ): BlockEntityTicker<T?>? {
        return BaseEntityBlock.createTickerHelper(
            type,
            CCXUBlockEntities.UPGRADE_STATION_ENTITY.get()
        ) {lvl, pos, state, be ->
            (be as UpgradeStationEntity).tick(lvl, pos, state, be)
        }
    }

    fun getRealShape(): VoxelShape {
        val shapes = listOf(
            Shapes.box(0.0, 10 * 0.0625, 0.0, 1.0, 12 * 0.0625, 1.0),
            Shapes.box(0.0, 0.0, 0.0, 2.0 * 0.0625, 10 * 0.0625, 2.0 * 0.0625),
            Shapes.box(14.0 * 0.0625, 0.0, 0.0, 16.0 * 0.0625, 10 * 0.0625, 2.0 * 0.0625),
            Shapes.box(0.0, 0.0, 14.0 * 0.0625, 2.0 * 0.0625, 10 * 0.0625, 16.0 * 0.0625),
            Shapes.box(14.0 * 0.0625, 0.0, 14.0 * 0.0625, 16.0 * 0.0625, 10 * 0.0625, 16.0 * 0.0625)
        )
        return shapes.reduce { acc, shape -> Shapes.or(acc, shape) }
    }
}