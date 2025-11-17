package com.awesoft.ccx_drones.client;


import com.awesoft.ccx_drones.entity.drone.DroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class DroneEntityModel extends EntityModel<DroneEntity> {
    public final ModelPart body;
    public final ModelPart prop1;
    public final ModelPart prop2;
    public final ModelPart prop3;
    public final ModelPart prop4;
	public final ModelPart modem;

	public DroneEntityModel(ModelPart root) {
		this.body = root.getChild("body");
		this.prop1 = this.body.getChild("prop1");
		this.prop2 = this.body.getChild("prop2");
		this.prop3 = this.body.getChild("prop3");
		this.prop4 = this.body.getChild("prop4");
		this.modem = this.body.getChild("modem");
	}

	public static LayerDefinition getTexturedData() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(88, 39).addBox(10.0F, -5.2778F, -13.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 88).addBox(-10.0F, -2.2778F, -10.0F, 20.0F, 5.0F, 20.0F, new CubeDeformation(0.0F))
		.texOffs(88, 52).addBox(-12.0F, -1.2778F, -12.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(88, 0).addBox(-13.0F, -5.2778F, -13.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(88, 59).addBox(8.0F, -1.2778F, -12.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(88, 66).addBox(-12.0F, -1.2778F, 8.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(88, 73).addBox(8.0F, -1.2778F, 8.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(88, 13).addBox(-13.0F, -5.2778F, 10.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(88, 26).addBox(10.0F, -5.2778F, 10.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(91, 15).addBox(11.0F, -6.2778F, 11.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(91, 15).addBox(11.0F, -6.2778F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(91, 15).addBox(-12.0F, -6.2778F, 11.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(91, 15).addBox(-12.0F, -6.2778F, -12.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.2778F, 0.0F));

		PartDefinition prop1 = body.addOrReplaceChild("prop1", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -4.0F, -11.0F, 22.0F, 0.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(11.5F, -3F, 11.5F));

		PartDefinition prop2 = body.addOrReplaceChild("prop2", CubeListBuilder.create().texOffs(0, 22).addBox(-11.0F, -4.0F, -11.0F, 22.0F, 0.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(11.5F, -3F, -11.5F));

		PartDefinition prop3 = body.addOrReplaceChild("prop3", CubeListBuilder.create().texOffs(0, 66).addBox(-11.0F, -4.0F, -11.0F, 22.0F, 0.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.5F, -3F, 11.5F));

		PartDefinition prop4 = body.addOrReplaceChild("prop4", CubeListBuilder.create().texOffs(0, 44).addBox(-11.0F, -4.0F, -11.0F, 22.0F, 0.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.5F, -3F, -11.5F));

		PartDefinition modem = body.addOrReplaceChild("modem", CubeListBuilder.create().texOffs(86, 86).addBox(-3.0F, -4.0F, 8.0F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(88, 80).addBox(-1.5F, -3.0F, 12.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.7222F, 0.0F));

		PartDefinition cube_r1 = modem.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(100, 80).addBox(-0.5F, -4.5F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.5F, 13.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition cube_r2 = modem.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(100, 80).addBox(-0.5F, -4.5F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.5F, 13.0F, 0.0F, 0.0F, 0.4363F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

    @Override
    public void setupAnim(DroneEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        prop1.yRot = (entity.propellerRotation * ((float)Math.PI / 180f));
        prop2.yRot = (entity.propellerRotation * ((float)Math.PI / 180f));
        prop3.yRot = (entity.propellerRotation * ((float)Math.PI / 180f));
        prop4.yRot = (entity.propellerRotation * ((float)Math.PI / 180f));
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        /*
        prop1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        prop2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        prop3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        prop4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        modem.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);*/
        poseStack.scale(0.75F,0.75F,0.75F);
        poseStack.translate(0F,0.25F,0F);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}