package com.awesoft.ccx.client.renderer;

import com.awesoft.ccx.block.pcie.PCIeBlockEntity;
import com.awesoft.ccx.block.rack.RackBlock;
import com.awesoft.ccx.block.rack.RackBlockEntity;
import com.awesoft.ccx.registry.CCXBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class PCIeRenderer implements BlockEntityRenderer<PCIeBlockEntity> {

    public PCIeRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(PCIeBlockEntity blockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        Minecraft mc = Minecraft.getInstance();

        for (int i = 0; i < 4; i++) {
            ItemStack stack = blockEntity.getInventory().getItem(i);
            if (!stack.isEmpty()) {

                int fullbright = 0xF000F0;

                poseStack.pushPose();

                BlockPos pos = blockEntity.getBlockPos();

                Direction facing = blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
                float degrees;

                switch (facing) {
                    case NORTH -> degrees = 90f;
                    case SOUTH -> degrees = 270f;
                    case WEST  -> degrees = 180f;
                    case EAST  -> degrees = 0f;
                    default    -> degrees = 90f;
                }

                poseStack.translate(0.5, 0, 0.5);

                Quaternionf rot = new Quaternionf().rotationXYZ(0f, (float)Math.toRadians(degrees), 0f);
                poseStack.mulPose(rot);

                float z0 = 0.35f;
                float z1 = 0.15f;
                float z2 = -0.15f;
                float z3 = -0.35f;

                if (i == 0) {
                    poseStack.translate(0.125, 0.75, z0);
                } else if (i == 1) {
                    poseStack.translate(0.125, 0.75, z1);
                } else if (i == 2) {
                    poseStack.translate(0.125, 0.75, z2);
                } else if (i == 3) {
                    poseStack.translate(0.125, 0.75, z3);
                }

                //poseStack.translate(1.125, 0.75, 1.35);


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
}
