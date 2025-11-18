package com.awesoft.ccx.client.renderer;

import com.awesoft.ccx.block.pcReader.PCReaderBlockEntity;
import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.joml.Quaternionf;

public class PCReaderRenderer implements BlockEntityRenderer<PCReaderBlockEntity> {

    public PCReaderRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(PCReaderBlockEntity blockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        Minecraft mc = Minecraft.getInstance();

        ItemStack stack = blockEntity.getInventory().getItem(0);
        if (!stack.isEmpty()) {

            int fullbright = 0xF000F0;

            poseStack.pushPose();

            BlockPos pos = blockEntity.getBlockPos();

            Direction facing = blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            float degrees;

            switch (facing) {
                case NORTH -> degrees = 0f;
                case SOUTH -> degrees = 180f;
                case WEST  -> degrees = 90f;
                case EAST  -> degrees = -90f;
                default    -> degrees = 0f;
            }

            poseStack.translate(0.5, 0, 0.5);

            poseStack.scale(0.5F, -0.5F, -0.5F);

            Quaternionf rot = new Quaternionf().rotationXYZ(0f, (float)Math.toRadians(degrees), 0f);
            poseStack.mulPose(rot);

            if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                poseStack.translate(0, -2, 0.44);
            } else {
                poseStack.translate(0, -2, -0.44);
            }

            //poseStack.translate(1.125, 0.75, 1.35);


            mc.getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    fullbright,
                    combinedOverlay,
                    poseStack,
                    buffer,
                    blockEntity.getLevel(),
                    0
            );

            poseStack.popPose();
        }

    }
}
