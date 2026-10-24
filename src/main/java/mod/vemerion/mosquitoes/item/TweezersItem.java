package mod.vemerion.mosquitoes.item;

import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.tick.Tick;
import mod.vemerion.mosquitoes.tick.Ticks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class TweezersItem extends Item {
	public static class Vec2f {
		public final float x, y;

		public Vec2f(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	private static final int DURATION = 72000;

	public TweezersItem() {
		setMaxDamage(16);
		setMaxStackSize(1);
		setCreativeTab(Main.TAB_MOSQUITOES);
		setTranslationKey(Main.MODID + ".tweezers_item");
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return DURATION;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

	public static Vec2f getPosition(float duration) {
		float p = duration % (15 * 5);
		if (p < 0)
			p += 15 * 5;
		int col = ((int) p) / 15;
		float x = col * 0.05f - 0.1f;
		float y = (p % 15.0f) * (0.2f / 15.0f) - 0.1f;
		return new Vec2f(x, y);
	}

	public static Vec2f getPosition(int progress) {
		return getPosition((float) progress);
	}

	public static float distance(Vec2f p1, Vec2f p2) {
		float x = p1.x - p2.x;
		float y = p1.y - p2.y;
		return x * x + y * y;
	}

	public static boolean isOverTick(Vec2f pos, Vec2f tickPos) {
		float dx = pos.x - tickPos.x;
		float dy = pos.y - tickPos.y;
		return (dx * dx + dy * dy < 0.0022f) || (Math.abs(dx) <= 0.040f && Math.abs(dy) <= 0.035f);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			Ticks ticks = Ticks.getTicks(player);
			if (ticks.hasTick()) {
				Tick tick = ticks.getTick();
				int duration = getMaxItemUseDuration(stack) - timeLeft;
				Vec2f pos = getPosition((float) duration);
				Vec2f tickPos = new Vec2f(tick.getX(0.5f), tick.getY(0.5f));
				if (isOverTick(pos, tickPos)) {
					if (!worldIn.isRemote) {
						worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, Main.TEAR_SOUND,
								SoundCategory.PLAYERS, 1.0f, 1.0f);
						ticks.sendRemoveMessage(player);
						stack.damageItem(1, player);
					} else {
						mod.vemerion.mosquitoes.network.Network.INSTANCE
								.sendToServer(new mod.vemerion.mosquitoes.network.PluckTickMessage());
						mod.vemerion.mosquitoes.network.ClientOnlyMethods.removeTick();
					}
				}
			}
		}
	}
}
