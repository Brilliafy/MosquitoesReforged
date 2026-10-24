package mod.vemerion.mosquitoes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mod.vemerion.mosquitoes.tick.Tick;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class TickModel extends ModelBase {
	// Super small scale for ticks
	public static final float SCALE = 0.007f;

	public ModelRenderer head;
	public ModelRenderer leftTooth;
	public ModelRenderer rightTooth;
	public ModelRenderer body;
	public ModelRenderer leftLeg1;
	public ModelRenderer leftLeg2;
	public ModelRenderer leftLeg3;
	public ModelRenderer leftLeg4;
	public ModelRenderer rightLeg1;
	public ModelRenderer rightLeg2;
	public ModelRenderer rightLeg3;
	public ModelRenderer rightLeg4;

	private Map<ModelRenderer, Float> legStartRot;
	private ModelRenderer[] legGroup1;
	private ModelRenderer[] legGroup2;

	public TickModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.leftLeg3 = new ModelRenderer(this, 0, 8);
		this.leftLeg3.setRotationPoint(2.0F, 1.0F, 4.1F);
		this.leftLeg3.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(leftLeg3, 0.0F, 0.11728612207217244F, -0.7819074915776542F);
		this.rightTooth = new ModelRenderer(this, 0, 0);
		this.rightTooth.setRotationPoint(-1.2F, 0.8F, -0.5F);
		this.rightTooth.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
		this.setRotateAngle(rightTooth, -0.5082398928281348F, 0.0F, 0.0F);
		this.leftLeg4 = new ModelRenderer(this, 0, 8);
		this.leftLeg4.setRotationPoint(2.0F, 1.0F, 5.4F);
		this.leftLeg4.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(leftLeg4, 0.23457224414434488F, -0.3909537457888271F, -0.9382889765773795F);
		this.head = new ModelRenderer(this, 15, 0);
		this.head.setRotationPoint(0.0F, 1.0F, -4.0F);
		this.head.addBox(-1.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
		this.leftLeg2 = new ModelRenderer(this, 0, 8);
		this.leftLeg2.setRotationPoint(2.0F, 1.0F, 2.8F);
		this.leftLeg2.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(leftLeg2, -0.03909537541112055F, 0.3127630032889644F, -0.8600982340775168F);
		this.leftTooth = new ModelRenderer(this, 0, 0);
		this.leftTooth.setRotationPoint(1.2F, 0.8F, -0.5F);
		this.leftTooth.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
		this.setRotateAngle(leftTooth, -0.4300491170387584F, -0.11728612207217244F, -5.235987638949542E-4F);
		this.rightLeg4 = new ModelRenderer(this, 0, 8);
		this.rightLeg4.setRotationPoint(-2.0F, 1.0F, 5.4F);
		this.rightLeg4.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(rightLeg4, 0.23457224414434488F, 0.3909537457888271F, 0.9382889765773795F);
		this.leftLeg1 = new ModelRenderer(this, 0, 8);
		this.leftLeg1.setRotationPoint(2.0F, 1.0F, 1.5F);
		this.leftLeg1.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(leftLeg1, -0.5864306020384839F, 0.4300491170387584F, -0.9773843811168246F);
		this.rightLeg2 = new ModelRenderer(this, 0, 8);
		this.rightLeg2.setRotationPoint(-2.0F, 1.0F, 2.8F);
		this.rightLeg2.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(rightLeg2, -0.27366763203903305F, -0.3127630032889644F, 0.8600982340775168F);
		this.body = new ModelRenderer(this, 0, 0);
		this.body.setRotationPoint(0.0F, 1.0F, 1.0F);
		this.body.addBox(-2.5F, -3.0F, 0.0F, 5, 3, 5, 0.0F);
		this.rightLeg1 = new ModelRenderer(this, 0, 8);
		this.rightLeg1.setRotationPoint(-2.0F, 1.0F, 1.5F);
		this.rightLeg1.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(rightLeg1, -0.7428121536172364F, -0.5759586531581287F, 0.9382889765773795F);
		this.rightLeg3 = new ModelRenderer(this, 0, 8);
		this.rightLeg3.setRotationPoint(-2.0F, 1.0F, 4.1F);
		this.rightLeg3.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
		this.setRotateAngle(rightLeg3, -0.0781907508222411F, -0.11728612207217244F, 0.7819074915776542F);
		this.head.addChild(this.leftLeg3);
		this.head.addChild(this.rightTooth);
		this.head.addChild(this.leftLeg4);
		this.head.addChild(this.leftLeg2);
		this.head.addChild(this.leftTooth);
		this.head.addChild(this.rightLeg4);
		this.head.addChild(this.leftLeg1);
		this.head.addChild(this.rightLeg2);
		this.head.addChild(this.rightLeg1);
		this.head.addChild(this.rightLeg3);

		legGroup1 = new ModelRenderer[] { leftLeg1, leftLeg3, rightLeg2, rightLeg4 };
		legGroup2 = new ModelRenderer[] { rightLeg1, rightLeg3, leftLeg2, leftLeg4 };
		legStartRot = new HashMap<>();
		for (ModelRenderer mr : legGroup1) {
			legStartRot.put(mr, mr.rotateAngleX);
		}
		for (ModelRenderer mr : legGroup2) {
			legStartRot.put(mr, mr.rotateAngleX);
		}
	}

	public void renderTick(Tick tick, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(tick.getX(partialTicks), tick.getY(partialTicks), -0.1f);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(tick.getDirection(), 0, 1, 0);
		GlStateManager.scale(SCALE, SCALE, SCALE);

		head.render(0.0625f);

		GlStateManager.pushMatrix();
		head.postRender(0.0625f);
		float bellySize = tick.getBellySize(partialTicks);
		GlStateManager.translate(0, 1.0f / 16.0f, 1.0f / 16.0f);
		GlStateManager.scale(bellySize, bellySize, bellySize);
		GlStateManager.translate(0, -1.0f / 16.0f, -1.0f / 16.0f);

		body.render(0.0625f);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	public void animate(Tick tick, float partialTicks) {
		float ageInTicks = tick.ticksExisted() + partialTicks;

		if (tick.isMoving()) {
			leftTooth.rotateAngleZ = MathHelper.sin(ageInTicks / 40.0f * (float) Math.PI * 2) * (float) Math.PI / 12;
			rightTooth.rotateAngleZ = -MathHelper.sin(ageInTicks / 40.0f * (float) Math.PI * 2) * (float) Math.PI / 12;

			Random rand = new Random(0);
			for (ModelRenderer mr : legGroup1) {
				mr.rotateAngleX = legStartRot.get(mr)
						+ MathHelper.sin((ageInTicks + rand.nextFloat() * 10) / 60.0f * (float) Math.PI * 2)
								* (float) Math.PI / 18;
			}
			for (ModelRenderer mr : legGroup2) {
				mr.rotateAngleX = legStartRot.get(mr)
						- MathHelper.sin((ageInTicks + rand.nextFloat() * 10) / 60.0f * (float) Math.PI * 2)
								* (float) Math.PI / 18;
			}
		} else {
			if (ageInTicks % 320 / 320.0f < 0.25f) {
				leftTooth.rotateAngleZ = MathHelper.sin(ageInTicks / 80.0f * (float) Math.PI * 2) * (float) Math.PI / 30;
				rightTooth.rotateAngleZ = -MathHelper.sin(ageInTicks / 80.0f * (float) Math.PI * 2) * (float) Math.PI / 30;
			}
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, net.minecraft.entity.Entity entityIn) {
		leftTooth.rotateAngleZ = MathHelper.sin(ageInTicks / 40.0f * (float) Math.PI * 2) * (float) Math.PI / 12;
		rightTooth.rotateAngleZ = -MathHelper.sin(ageInTicks / 40.0f * (float) Math.PI * 2) * (float) Math.PI / 12;

		float legSwing = MathHelper.sin(limbSwing * 1.5F) * 0.4F * limbSwingAmount;
		for (ModelRenderer mr : legGroup1) {
			Float base = legStartRot.get(mr);
			mr.rotateAngleX = (base != null ? base : 0.0F) + legSwing;
		}
		for (ModelRenderer mr : legGroup2) {
			Float base = legStartRot.get(mr);
			mr.rotateAngleX = (base != null ? base : 0.0F) - legSwing;
		}
	}

	@Override
	public void render(net.minecraft.entity.Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
		GlStateManager.pushMatrix();
		// Model feet are at Y = 4.0; standard mob ground is at Y = 24.0. Translate by 20 * scale to place feet on the ground!
		GlStateManager.translate(0.0F, 20.0F * scale, 0.0F);
		head.render(scale);
		GlStateManager.pushMatrix();
		head.postRender(scale);
		body.render(scale);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
