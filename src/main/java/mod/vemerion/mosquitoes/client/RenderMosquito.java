package mod.vemerion.mosquitoes.client;

import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.entity.EntityMosquito;
import mod.vemerion.mosquitoes.model.MosquitoModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderMosquito extends RenderLiving<EntityMosquito> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/mosquito.png");

	public RenderMosquito(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new MosquitoModel(), 0.02F);
	}

	@Override
	protected void preRenderCallback(EntityMosquito entitylivingbaseIn, float partialTickTime) {
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
		GlStateManager.scale(0.09F, 0.09F, 0.09F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMosquito entity) {
		return TEXTURE;
	}
}
