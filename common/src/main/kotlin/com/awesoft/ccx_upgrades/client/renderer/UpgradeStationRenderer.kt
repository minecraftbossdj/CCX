package com.awesoft.ccx_upgrades.client.renderer

import com.awesoft.ccx.registry.CCXItems
import com.awesoft.ccx_upgrades.block.upgradeStation.UpgradeStation
import com.awesoft.ccx_upgrades.block.upgradeStation.UpgradeStationEntity
import com.awesoft.ccx_upgrades.lib.rotate
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.joml.Vector3f

class UpgradeStationRenderer(ctx: BlockEntityRendererProvider.Context?): BlockEntityRenderer<UpgradeStationEntity> {
    override fun render(
        blockEntity: UpgradeStationEntity,
        tickDelta: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int
    ) {
        val level = blockEntity.level ?: return
        val mc = Minecraft.getInstance()




        //rotation shii
        val axis = Vector3f(0f, 1f, 0f)
        var rotationDegrees: Double = 0.0

        val facing = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        rotationDegrees = when (facing) {
            Direction.DOWN -> 0.0
            Direction.UP -> 0.0
            Direction.NORTH -> 0.0
            Direction.SOUTH -> 180.0
            Direction.WEST -> 90.0
            Direction.EAST -> -90.0
        }

        val angle = Math.toRadians(rotationDegrees).toFloat()

        val rotation = Quaternionf(AxisAngle4f(angle, axis.x(), axis.y(), axis.z()))


        //render time
        poseStack.pushPose()

        poseStack.rotateAround(rotation,0.5f,0.5f,0.5f)
        poseStack.translate(13*0.0625, 16*0.0625, 9*0.0625)

        var itemOne: ItemStack = blockEntity.getItem(0) ?: ItemStack(Items.AIR)

        val time1 = (level.gameTime + tickDelta) % 360
        poseStack.mulPose(Axis.YP.rotationDegrees(time1))

        val size = 0.5f

        poseStack.scale(size, size, size)

        mc.itemRenderer.renderStatic(
            itemOne,
            ItemDisplayContext.FIXED,
            0xFFFFFF,
            combinedOverlay,
            poseStack,
            buffer,
            blockEntity.getLevel(),
            0
        )

        poseStack.popPose()

        //secon items
        poseStack.pushPose()

        poseStack.rotateAround(rotation,0.5f,0.5f,0.5f)
        poseStack.translate(3*0.0625, 16*0.0625, 9*0.0625)

        val time2 = (level.gameTime + tickDelta) % 360
        poseStack.mulPose(Axis.YP.rotationDegrees(time2))

        poseStack.scale(size, size, size)

        var itemTwo: ItemStack = blockEntity.getItem(1) ?: ItemStack(Items.AIR)

        mc.itemRenderer.renderStatic(
            itemTwo,
            ItemDisplayContext.FIXED,
            0xFFFFFF,
            combinedOverlay,
            poseStack,
            buffer,
            blockEntity.getLevel(),
            0
        )

        poseStack.popPose()

        //result item, slot 3 (slot 2 in java)
        poseStack.pushPose()

        poseStack.rotateAround(rotation,0.5f,0.5f,0.5f)
        poseStack.translate(8*0.0625, 16*0.0625, 4*0.0625)

        val time3 = (level.gameTime + tickDelta) % 360
        poseStack.mulPose(Axis.YP.rotationDegrees(time3))

        poseStack.scale(size, size, size)

        var itemThree: ItemStack = blockEntity.getItem(2) ?: ItemStack(Items.AIR)

        mc.itemRenderer.renderStatic(
            itemThree,
            ItemDisplayContext.FIXED,
            0xFFFFFF,
            combinedOverlay,
            poseStack,
            buffer,
            blockEntity.getLevel(),
            0
        )

        poseStack.popPose()



        //kube hehe
        poseStack.pushPose()

        poseStack.rotateAround(rotation,0.5f,0.5f,0.5f)

        //drawWireCube(poseStack, buffer, 10*0.0625, 13*0.0625, 6*0.0625, 6*0.0625, 17*0.0625, 2*0.0625, color, color, color, 1f)

        slotCube(blockEntity.blockPos, poseStack, buffer, rotation)

        poseStack.popPose()

    }



    fun slotCube(blockPos: BlockPos, poseStack: PoseStack, buffer: MultiBufferSource, rotation: Quaternionf) {

        val mc = Minecraft.getInstance()
        val player = mc.player ?: return

        val eyePos = player.eyePosition
        val lookVec = player.lookAngle
        val maxDistance = 5.0

        val slots = listOf(
            Triple( // slot 1
                Vec3(11 * 0.0625, 13 * 0.0625, 7 * 0.0625),
                Vec3(15 * 0.0625, 17 * 0.0625, 11 * 0.0625),
                1
            ),
            Triple( // slot 2
                Vec3(5 * 0.0625, 13 * 0.0625, 11 * 0.0625),
                Vec3(1 * 0.0625, 17 * 0.0625, 7 * 0.0625),
                2
            ),
            Triple( // slot 3
                Vec3(10 * 0.0625, 13 * 0.0625, 6 * 0.0625),
                Vec3(6 * 0.0625, 17 * 0.0625, 2 * 0.0625),
                3
            )
        )

        val blockVec = Vec3(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())

        var closestSlot: Triple<Vec3, Vec3, Int>? = null
        var closestDistance = Double.MAX_VALUE

        val rayEnd = eyePos.add(lookVec.scale(maxDistance))

        for ((minLocal, maxLocal, slotId) in slots) {
            val rotatedMin = minLocal.rotate(rotation)
            val rotatedMax = maxLocal.rotate(rotation)

            val minWorld = blockVec.add(rotatedMin)
            val maxWorld = blockVec.add(rotatedMax)

            val aabb = AABB(minWorld, maxWorld)

            val hit = aabb.clip(eyePos, rayEnd)
            if (hit.isPresent) {
                val distance = eyePos.distanceToSqr(hit.get())
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestSlot = Triple(minLocal, maxLocal, slotId)
                }
            }
        }

        closestSlot?.let { (minLocal, maxLocal, _) ->
            drawWireCube(
                poseStack,
                buffer,
                minLocal.x,
                minLocal.y,
                minLocal.z,
                maxLocal.x,
                maxLocal.y,
                maxLocal.z,
                0.125f,
                0.125f,
                0.125f,
                1f
            )
        }
    }

    fun drawWireCube(
        matrices: PoseStack,
        buffer: MultiBufferSource,
        minX: Double, minY: Double, minZ: Double,
        maxX: Double, maxY: Double, maxZ: Double,
        r: Float, g: Float, b: Float, a: Float
    ) {
        val vertexConsumer: VertexConsumer = buffer.getBuffer(RenderType.lines())

        val box = AABB(minX, minY, minZ, maxX, maxY, maxZ)

        LevelRenderer.renderLineBox(
            matrices,
            vertexConsumer,
            box,
            r, g, b, a
        )
    }

}