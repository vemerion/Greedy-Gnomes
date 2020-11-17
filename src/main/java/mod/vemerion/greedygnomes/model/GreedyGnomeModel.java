package mod.vemerion.greedygnomes.model;

import com.google.common.collect.ImmutableList;

import mod.vemerion.greedygnomes.entity.GreedyGnomeEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/**
 * Created using Tabula 8.0.0
 */
public class GreedyGnomeModel extends EntityModel<GreedyGnomeEntity> {
	public ModelPart head;
	public ModelPart body;
	public ModelPart leftArm;
	public ModelPart leftLeg;
	public ModelPart rightArm;
	public ModelPart rightLeg;
	public ModelPart leftEar;
	public ModelPart rightEar;
	public ModelPart beard;
	public ModelPart nose;
	public ModelPart hat1;
	public ModelPart hat2;

	public GreedyGnomeModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.body = new ModelPart(this, 16, 16);
		this.body.setPivot(0.0F, 8.0F, 0.0F);
		this.body.addCuboid(-3.0F, 0.0F, -2.0F, 6.0F, 8.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		this.hat1 = new ModelPart(this, 0, 28);
		this.hat1.setPivot(0.0F, -8.0F, 0.0F);
		this.hat1.addCuboid(-5.0F, -1.0F, -5.0F, 10.0F, 1.0F, 10.0F, 0.0F, 0.0F, 0.0F);
		this.leftEar = new ModelPart(this, 0, 0);
		this.leftEar.setPivot(4.0F, -4.5F, -2.0F);
		this.leftEar.addCuboid(0.0F, -1.5F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.head = new ModelPart(this, 0, 0);
		this.head.setPivot(0.0F, 8.0F, 0.0F);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F);
		this.leftArm = new ModelPart(this, 44, 0);
		this.leftArm.setPivot(3.0F, 8.0F, 0.0F);
		this.leftArm.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.rightLeg = new ModelPart(this, 52, 0);
		this.rightLeg.setPivot(-1.9F, 16.0F, 0.0F);
		this.rightLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.beard = new ModelPart(this, 24, 0);
		this.beard.setPivot(0.0F, -3.0F, -3.0F);
		this.beard.addCuboid(-5.0F, 0.0F, 0.0F, 10.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		this.rightArm = new ModelPart(this, 44, 0);
		this.rightArm.setPivot(-3.0F, 8.0F, 0.0F);
		this.rightArm.addCuboid(-2.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.rightEar = new ModelPart(this, 0, 0);
		this.rightEar.mirror = true;
		this.rightEar.setPivot(-4.0F, -4.5F, -2.0F);
		this.rightEar.addCuboid(-2.0F, -1.5F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.nose = new ModelPart(this, 0, 4);
		this.nose.mirror = true;
		this.nose.setPivot(0.0F, -3.0F, -4.0F);
		this.nose.addCuboid(-1.0F, -1.5F, -2.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.hat2 = new ModelPart(this, 0, 39);
		this.hat2.setPivot(0.0F, -1.0F, 0.0F);
		this.hat2.addCuboid(-4.0F, -3.0F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, 0.0F, 0.0F);
		this.leftLeg = new ModelPart(this, 52, 0);
		this.leftLeg.setPivot(1.9F, 16.0F, 0.0F);
		this.leftLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.head.addChild(this.hat1);
		this.head.addChild(this.leftEar);
		this.head.addChild(this.beard);
		this.head.addChild(this.rightEar);
		this.head.addChild(this.nose);
		this.hat1.addChild(this.hat2);
	}

	@Override
	public void render(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		ImmutableList.of(this.body, this.head, this.leftArm, this.rightLeg, this.rightArm, this.leftLeg)
				.forEach((ModelPart) -> {
					ModelPart.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				});
	}

	@Override
	public void animateModel(GreedyGnomeEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		if (entity.isCollecting()) {
			float collectingProgress = entity.getCollectingProgress(tickDelta);
			head.yaw = 0;
			head.pitch = 0;
			rightArm.pitch = 0;
			leftArm.pitch = 0;
			leftArm.roll = MathHelper.cos(collectingProgress / 3) * (float) Math.PI / 8 - (float) Math.PI / 6;
			rightArm.roll = MathHelper.cos(collectingProgress / 3) * (float) Math.PI / 8 + (float) Math.PI / 6;
			hat1.pitch = collectingProgress / 20 * (float) Math.PI * 2;
			hat1.pivotY = -8 - MathHelper.sin(collectingProgress / 40 * (float) Math.PI) * 20;
			rightLeg.pitch = 0;
			leftLeg.pitch = 0;
		}
	}

	@Override
	public void setAngles(GreedyGnomeEntity entity, float limbAngle, float limbDistance, float animationProgress,
			float headYaw, float headPitch) {
		if (!entity.isCollecting()) {
			rightArm.pitch = MathHelper.cos(limbAngle * 0.6662f + (float) Math.PI) * 2f * limbDistance * 0.5f;
			leftArm.pitch = MathHelper.cos(limbAngle * 0.6662f) * 2f * limbDistance * 0.5f;
			rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
			leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float) Math.PI) * 1.4f * limbDistance;
			leftArm.yaw = 0;
			rightArm.yaw = 0;
			leftArm.roll = 0;
			rightArm.roll = 0;
			hat1.pitch = 0;
			hat1.pivotY = -8;

			head.pitch = headPitch * 0.0175f;
			head.yaw = headYaw * 0.005f;
		}

	}
}
