package mod.vemerion.mosquitoes.client;

import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.entity.EntityTick;
import mod.vemerion.mosquitoes.model.TickModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderTick extends RenderLiving<EntityTick> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/tick.png");

	public RenderTick(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new TickModel(), 0.02F);
	}

	@Override
	protected void preRenderCallback(EntityTick entitylivingbaseIn, float partialTickTime) {
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
		GlStateManager.scale(0.30F, 0.30F, 0.30F);
		if (entitylivingbaseIn.isRiding()) {
			net.minecraft.entity.Entity host = entitylivingbaseIn.getRidingEntity();
			if (host != null && host.height > host.width * 1.4F) {
				// Clinging flat against the vertical back/torso of a biped / villager
				GlStateManager.rotate(-75.0F, 1.0F, 0.0F, 0.0F);
			}
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTick entity) {
		return TEXTURE;
	}
}
