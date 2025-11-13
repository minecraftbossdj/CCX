package com.awesoft.ccx.client.renderer;

import com.awesoft.ccx.CCX;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class RackRenderer implements BlockEntityRenderer<RackBlockEntity> {

    public RackRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(RackBlockEntity blockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        Minecraft mc = Minecraft.getInstance();

        for (int i = 0; i < 4; i++) {
            ItemStack stack = blockEntity.getInventory().getItem(i);
            if (!stack.isEmpty()) {

                BlockState serverState = CCXBlocks.RACK.get()
                        .defaultBlockState()
                        .setValue(RackBlock.SERVER_SLOT, i+1);

                BakedModel model = mc.getModelManager().getBlockModelShaper().getBlockModel(serverState);

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

                Quaternionf rot = new Quaternionf().rotationXYZ(0f, (float)Math.toRadians(degrees), 0f);
                poseStack.mulPose(rot);

                poseStack.translate(-0.5, 0, -0.5);

                mc.getBlockRenderer().getModelRenderer().renderModel(
                        poseStack.last(),
                        buffer.getBuffer(RenderType.translucent()),
                        serverState,
                        model,
                        1.0f, 1.0f, 1.0f,
                        fullbright, combinedOverlay
                ); //don't judge me okay? I tried my HARDEST to get json loading (so like ccx:blocks/server_1) to work, and it didn't wanna, so I resorted to... this

                poseStack.popPose();
            }
        }
    }
}
