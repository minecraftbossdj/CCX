package com.awesoft.ccx.client.renderer;

import com.awesoft.ccx.CCX;
import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.awesoft.ccx.block.usb.USBBlockEntity;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

public class UsbPortRenderer implements BlockEntityRenderer<USBBlockEntity> {

    public UsbPortRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(USBBlockEntity blockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        Minecraft mc = Minecraft.getInstance();

        ItemStack stack = blockEntity.getInventory().getItem(0);
        if (!stack.isEmpty()) {

            int fullbright = 0xF000F0;

            poseStack.pushPose();

            BlockPos pos = blockEntity.getBlockPos();

            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.FACING);

            float xRot = 0f;
            float yRot = 0f;

            switch (facing) {
                case NORTH -> yRot = 0f;
                case SOUTH -> yRot = 180f;
                case WEST  -> yRot = 90;
                case EAST  -> yRot = 270f;

                case UP -> xRot = 90f;
                case DOWN -> xRot = -90f;
            }

            poseStack.translate(0.5, 0, 0.5);

            Quaternionf rot = new Quaternionf().rotationXYZ((float)Math.toRadians(xRot), (float)Math.toRadians(yRot), 0f);
            poseStack.mulPose(rot);

            if (facing == Direction.UP) {
                poseStack.translate(0, 0.375, -0.5);
            } else if (facing == Direction.DOWN ) {
                poseStack.translate(0, 0.375, 0.5);
            } else {
                poseStack.translate(0, 0.875, 0);
            }

            float size = 2F;
            poseStack.scale(size,size,size);


            mc.getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    combinedLight,
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
