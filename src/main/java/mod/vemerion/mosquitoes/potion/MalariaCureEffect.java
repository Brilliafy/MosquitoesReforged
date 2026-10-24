package mod.vemerion.mosquitoes.potion;

import javax.annotation.Nullable;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MalariaCureEffect extends Potion {

	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID, "textures/mob_effect/malaria_cure_effect.png");

	public MalariaCureEffect() {
		super(false, 0x00FFAC);
		setPotionName("effect." + Main.MODID + ".malaria_cure_effect");
	}

	@Override
	public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entityLivingBaseIn,
			int amplifier, double health) {
		entityLivingBaseIn.removePotionEffect(Main.MALARIA_EFFECT);
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		mc.getTextureManager().bindTexture(ICON);
		Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
		mc.getTextureManager().bindTexture(ICON);
		Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
	}
}
