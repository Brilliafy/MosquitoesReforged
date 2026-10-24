package mod.vemerion.mosquitoes.potion;

import java.util.ArrayList;
import java.util.List;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MalariaEffect extends Potion {

	private static final Potion[] EFFECTS = { MobEffects.BLINDNESS, MobEffects.NAUSEA, MobEffects.POISON, MobEffects.SLOWNESS };
	private static final ResourceLocation ICON = new ResourceLocation(Main.MODID, "textures/mob_effect/malaria_effect.png");

	public MalariaEffect() {
		super(true, 0x4B3B2B);
		setPotionName("effect." + Main.MODID + ".malaria_effect");
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		entityLivingBaseIn.addPotionEffect(new PotionEffect(EFFECTS[entityLivingBaseIn.getRNG().nextInt(EFFECTS.length)], 20 * 60, amplifier));
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % (20 * 60 * 6) == 0;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
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
