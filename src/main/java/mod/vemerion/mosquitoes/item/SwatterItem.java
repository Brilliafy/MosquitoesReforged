package mod.vemerion.mosquitoes.item;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SwatterItem extends Item {
	private static final int LONG_TIME = 20 * 60 * 60;

	public SwatterItem() {
		setMaxDamage(16);
		setMaxStackSize(1);
		setCreativeTab(Main.TAB_MOSQUITOES);
		setTranslationKey(Main.MODID + ".swatter_item");
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return LONG_TIME;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entityLiving, int count) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			World world = player.world;
			if (!world.isRemote) {
				double reach = 3.0D;
				java.util.List<mod.vemerion.mosquitoes.entity.EntityMosquito> mosquitoes = world.getEntitiesWithinAABB(
						mod.vemerion.mosquitoes.entity.EntityMosquito.class,
						player.getEntityBoundingBox().grow(reach, reach, reach));
				for (mod.vemerion.mosquitoes.entity.EntityMosquito mosquito : mosquitoes) {
					if (mosquito.isEntityAlive() && player.getDistanceSq(mosquito) <= (reach * reach)) {
						mosquito.attackEntityFrom(net.minecraft.util.DamageSource.causePlayerDamage(player), 10.0F);
						stack.damageItem(1, player);
						player.swingArm(player.getActiveHand());
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		stack.damageItem(1, attacker);
		if (attacker != null && !attacker.world.isRemote && target != null) {
			if (!(target instanceof mod.vemerion.mosquitoes.entity.EntityMosquito)
					&& !(target instanceof mod.vemerion.mosquitoes.entity.EntityTick)) {
				attacker.world.playSound(null, target.posX, target.posY, target.posZ, Main.SMACK_SOUND,
						net.minecraft.util.SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}
		return true;
	}
}
