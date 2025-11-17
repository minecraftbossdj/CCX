package com.awesoft.ccx_drones.client.render;

import com.awesoft.ccx_drones.client.CCXDronesClient;
import com.awesoft.ccx_drones.client.DroneEntityModel;
import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class DroneEntityRenderer extends MobRenderer<DroneEntity, DroneEntityModel> {

    public DroneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneEntityModel(context.bakeLayer(CCXDronesClient.MODEL_DRONE_LAYER)), 0.5f);
    }

    public static boolean hasUpgradeClient(DroneEntity mob, String upgrade) {
        Item upgradeItem = BuiltInRegistries.ITEM.get(new ResourceLocation(upgrade));
        if (upgradeItem == null || upgradeItem == Items.AIR) return false;

        for (int i = 0; i < 7; i++) {
            ItemStack item = mob.getSlotForRenderer(i);
            if (item.is(upgradeItem)) return true;
        }

        return false;
    }

    @Override
    public void render(DroneEntity mob, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        super.render(mob, f, g, poseStack, multiBufferSource, i);

        if (mob.isCarryingBlock()) {
            BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(),mob.getEntityData().get(DroneEntity.EXTRA).getCompound("carryingState"));

            poseStack.pushPose();
            poseStack.translate(-0.5,-1,-0.5);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.CHAIN.defaultBlockState(),poseStack,multiBufferSource,i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(-0.5,-2,-0.5);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state,poseStack,multiBufferSource,i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        poseStack.scale(1.25F,1F,1.25F);


        //System.out.println(mob.getEntityData());

        this.model.modem.visible = hasUpgradeClient(mob, "ccx_drones:modem_upgrade");

        if(!mob.getPassengers().isEmpty())
        {
            poseStack.pushPose();
            poseStack.translate(-0.5,-1,-0.5);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.CHAIN.defaultBlockState(),poseStack,multiBufferSource,i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }


    }

    @Override
    public ResourceLocation getTextureLocation(DroneEntity entity) {
        return new ResourceLocation("ccx_drones","textures/entity/drone_new.png");
    }
}
