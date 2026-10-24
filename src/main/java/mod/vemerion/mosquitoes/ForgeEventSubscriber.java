package mod.vemerion.mosquitoes;

import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import mod.vemerion.mosquitoes.mosquito.MosquitoesProvider;
import mod.vemerion.mosquitoes.network.Network;
import mod.vemerion.mosquitoes.network.SynchMosquitoesMessage;
import mod.vemerion.mosquitoes.tick.Ticks;
import mod.vemerion.mosquitoes.tick.TicksProvider;
import java.util.List;

import mod.vemerion.mosquitoes.entity.EntityMosquito;
import mod.vemerion.mosquitoes.entity.EntityTick;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@EventBusSubscriber(modid = Main.MODID)
public class ForgeEventSubscriber {
	public static final ResourceLocation MOSQUITOES_CAP = new ResourceLocation(Main.MODID, "mosquitoes");
	public static final ResourceLocation TICKS_CAP = new ResourceLocation(Main.MODID, "ticks");

	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(MOSQUITOES_CAP, new MosquitoesProvider());
			event.addCapability(TICKS_CAP, new TicksProvider());
		}
	}

	@SubscribeEvent
	public static void synchCapabilitiesWithClient(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		if (player instanceof EntityPlayerMP) {
			Mosquitoes mosquitoes = Mosquitoes.getMosquitoes(player);
			Network.INSTANCE.sendTo(new SynchMosquitoesMessage(mosquitoes.save()), (EntityPlayerMP) player);
			Ticks.getTicks(player).sendSyncMessage(player);
		}
	}

	@SubscribeEvent
	public static void synchCapabilitiesWithClient(PlayerChangedDimensionEvent event) {
		EntityPlayer player = event.player;
		if (player instanceof EntityPlayerMP) {
			Mosquitoes mosquitoes = Mosquitoes.getMosquitoes(player);
			Network.INSTANCE.sendTo(new SynchMosquitoesMessage(mosquitoes.save()), (EntityPlayerMP) player);
			Ticks.getTicks(player).sendSyncMessage(player);
		}
	}

	@SubscribeEvent
	public static void synchTicksWithClient(PlayerRespawnEvent event) {
		EntityPlayer player = event.player;
		if (player instanceof EntityPlayerMP) {
			Ticks.getTicks(player).sendSyncMessage(player);
		}
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			Ticks ticks = Ticks.getTicks(player);
			if (ticks.hasTick()) {
				if (!player.world.isRemote) {
					// Drop / spawn tick mob in the world where the player died
					EntityTick tickMob = new EntityTick(player.world);
					tickMob.setLocationAndAngles(player.posX, player.posY + 0.1D, player.posZ, player.rotationYaw, 0.0F);
					tickMob.motionX = (player.world.rand.nextDouble() - 0.5D) * 0.1D;
					tickMob.motionY = 0.15D;
					tickMob.motionZ = (player.world.rand.nextDouble() - 0.5D) * 0.1D;
					player.world.spawnEntity(tickMob);
					ticks.sendRemoveMessage(player);
				} else {
					ticks.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public static void retainTickAfterDeath(PlayerEvent.Clone event) {
		if (!event.isWasDeath()) {
			Ticks.getTicks(event.getEntityPlayer()).load(Ticks.getTicks(event.getOriginal()).save());
		}
	}

	@SubscribeEvent
	public static void tickCapabilities(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			EntityPlayer player = event.player;
			Mosquitoes.getMosquitoes(player).tick(player);
			Ticks.getTicks(player).tick(player);
		}
	}

	@SubscribeEvent
	public static void onAttackEntity(AttackEntityEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player == null)
			return;

		ItemStack held = player.getHeldItemMainhand();
		if (held.getItem() != Main.TWEEZERS_ITEM) {
			held = player.getHeldItemOffhand();
		}

		if (held.getItem() == Main.TWEEZERS_ITEM) {
			Entity target = event.getTarget();
			EntityTick tickToPluck = null;
			if (target instanceof EntityTick) {
				tickToPluck = (EntityTick) target;
			} else if (target != null) {
				for (Entity passenger : target.getPassengers()) {
					if (passenger instanceof EntityTick) {
						tickToPluck = (EntityTick) passenger;
						break;
					}
				}
			}

			if (tickToPluck != null) {
				event.setCanceled(true); // Spare the host mob!
				held.damageItem(1, player);
				player.swingArm(held == player.getHeldItemMainhand() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
				player.world.playSound(null, tickToPluck.posX, tickToPluck.posY, tickToPluck.posZ,
						Main.TEAR_SOUND, SoundCategory.PLAYERS, 1.0F, 1.0F);

				if (tickToPluck.isRiding()) {
					tickToPluck.dismountRidingEntity();
					tickToPluck.motionY = 0.25D;
					tickToPluck.motionX = (player.posX - tickToPluck.posX) * -0.1D;
					tickToPluck.motionZ = (player.posZ - tickToPluck.posZ) * -0.1D;
				}

				tickToPluck.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F);
			}
		}
	}
}
