package mod.vemerion.mosquitoes;

import mod.vemerion.mosquitoes.entity.EntityTick;
import mod.vemerion.mosquitoes.item.TweezersItem;
import mod.vemerion.mosquitoes.model.MosquitoModel;
import mod.vemerion.mosquitoes.model.TickModel;
import mod.vemerion.mosquitoes.mosquito.Mosquito;
import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import mod.vemerion.mosquitoes.network.ClientOnlyMethods;
import mod.vemerion.mosquitoes.network.Network;
import mod.vemerion.mosquitoes.network.PluckMobTickMessage;
import mod.vemerion.mosquitoes.network.PluckTickMessage;
import mod.vemerion.mosquitoes.network.SwatMosquitoMessage;
import mod.vemerion.mosquitoes.tick.Tick;
import mod.vemerion.mosquitoes.tick.Ticks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = Main.MODID, value = Side.CLIENT)
public class ClientForgeEventSubscriber {

	private static final ResourceLocation MOSQUITO_TEXTURE = new ResourceLocation(Main.MODID,
			"textures/entity/mosquito.png");
	private static final ResourceLocation TICK_TEXTURE = new ResourceLocation(Main.MODID, "textures/entity/tick.png");

	private static final MosquitoModel MOSQUITO_MODEL = new MosquitoModel();
	private static final TickModel TICK_MODEL = new TickModel();
	private static int lastSwatTick = -1;
	private static int tweezersPinchTicks = 0;
	private static int lastTweezersClickTime = -1;

	@SubscribeEvent
	public static void renderMosquito(RenderHandEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null)
			return;

		Mosquitoes mosquitoes = Mosquitoes.getMosquitoes(player);
		if (mosquitoes.count() == 0)
			return;

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(MOSQUITO_TEXTURE);

		for (int i = 0; i < mosquitoes.count(); i++) {
			Mosquito mosquito = mosquitoes.get(i);
			MOSQUITO_MODEL.animate(mosquito, event.getPartialTicks());
			MOSQUITO_MODEL.renderMosquito(mosquito, event.getPartialTicks());
		}
		GlStateManager.disableBlend();
	}

	@SubscribeEvent
	public static void renderSwatter(RenderSpecificHandEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null)
			return;

		ItemStack itemStack = event.getItemStack();
		Item item = itemStack.getItem();
		float partialTicks = event.getPartialTicks();
		if (item == Main.SWATTER_ITEM && player.isHandActive() && player.getActiveItemStack() == itemStack) {
			event.setCanceled(true);
			int maxDuration = itemStack.getMaxItemUseDuration();
			float duration = (float) maxDuration - ((float) player.getItemInUseCount() - partialTicks + 1.0f);
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, -0.2, -0.3);
			GlStateManager.translate(0, -1, 0);
			GlStateManager.rotate(Math.abs(MathHelper.cos((duration / 14.0f) * (float) Math.PI * 2.0f)) * -30.0f, 1, 0, 0);
			GlStateManager.translate(0, 1, 0);
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, itemStack,
					ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, false);
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public static void renderTweezers(RenderSpecificHandEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null)
			return;

		ItemStack itemStack = event.getItemStack();
		Item item = itemStack.getItem();
		float partialTicks = event.getPartialTicks();
		if (item == Main.TWEEZERS_ITEM && player.isHandActive() && player.getActiveItemStack() == itemStack) {
			event.setCanceled(true);
			int maxDuration = itemStack.getMaxItemUseDuration();
			float duration = (float) maxDuration - ((float) player.getItemInUseCount() - partialTicks + 1.0f);
			GlStateManager.pushMatrix();
			TweezersItem.Vec2f position = TweezersItem.getPosition(duration);
			GlStateManager.translate(position.x, position.y, -0.085);
			GlStateManager.scale(0.1f, 0.1f, 0.1f);
			if (tweezersPinchTicks > 0) {
				float pinch = (tweezersPinchTicks - partialTicks) / 4.0f;
				if (pinch > 0) {
					GlStateManager.rotate(pinch * 15.0f, 0, 0, 1);
				}
			}
			GlStateManager.translate(0, -1, 0);
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.translate(0, 1, 0);
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, itemStack,
					ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, false);
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public static void renderWaving(RenderSpecificHandEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null)
			return;

		float swingProgress = event.getSwingProgress();
		if (player.getHeldItemMainhand().isEmpty() && player.getHeldItemOffhand().isEmpty() && swingProgress > 0
				&& hasMosquitoes(player)) {
			event.setCanceled(true);
			renderWavingHands(player, swingProgress);
		}
	}

	@SubscribeEvent
	public static void renderTick(RenderHandEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null || player.isDead || player.getHealth() <= 0.0F)
			return;

		Ticks ticks = Ticks.getTicks(player);
		if (ticks.hasTick()) {
			Tick tick = ticks.getTick();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			Minecraft.getMinecraft().getTextureManager().bindTexture(TICK_TEXTURE);

			TICK_MODEL.animate(tick, event.getPartialTicks());
			TICK_MODEL.renderTick(tick, event.getPartialTicks());
			GlStateManager.disableBlend();
		}
	}

	private static void renderWavingHands(EntityPlayerSP player, float swing) {
		ModelPlayer playermodel = new ModelPlayer(0.0f, false);
		playermodel.setRotationAngles(0, 0, 0, 0, 0, 0.0625f, player);
		renderWavingHand(player, swing, EnumHandSide.RIGHT, playermodel.bipedRightArm, playermodel.bipedRightArmwear);
		renderWavingHand(player, swing, EnumHandSide.LEFT, playermodel.bipedLeftArm, playermodel.bipedLeftArmwear);
	}

	private static void renderWavingHand(EntityPlayerSP player, float swing, EnumHandSide handSide, ModelRenderer arm,
			ModelRenderer armwear) {
		float progress = MathHelper.sqrt(swing);
		boolean left = handSide == EnumHandSide.LEFT;
		float offset = !left ? 1 : -1;
		swing = left ? swing * 0.9f : swing;
		GlStateManager.pushMatrix();
		GlStateManager.translate(MathHelper.clampedLerp(1.6, 0.8, progress) * offset,
				-0.65 + swing * 0.1 + (left ? -0.1 : 0), -1);
		GlStateManager.rotate(-25 + swing * 6, 1, 0, 0);
		GlStateManager.rotate(swing * 50 * offset, 0, 0, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
		arm.render(0.0625f);
		armwear.render(0.0625f);
		GlStateManager.popMatrix();
	}

	@SubscribeEvent
	public static void onMouseEvent(MouseEvent event) {
		if (event.isButtonstate() && event.getButton() == 0) { // Left click DOWN
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if (player != null) {
				if (player.isHandActive() && player.getActiveItemStack().getItem() == Main.TWEEZERS_ITEM) {
					if (handleTweezersClick(player)) {
						event.setCanceled(true);
					}
				} else {
					ItemStack held = player.getHeldItemMainhand();
					if (held.getItem() != Main.TWEEZERS_ITEM) {
						held = player.getHeldItemOffhand();
					}
					if (held.getItem() == Main.TWEEZERS_ITEM) {
						if (handleWorldTweezersClick(player)) {
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player != null) {
			ItemStack held = player.getHeldItemMainhand();
			if (held.getItem() != Main.TWEEZERS_ITEM) {
				held = player.getHeldItemOffhand();
			}
			if (held.getItem() == Main.TWEEZERS_ITEM) {
				handleWorldTweezersClick(player);
			}
		}
		handleLeftClick(player);
	}

	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getWorld().isRemote) {
			EntityPlayer player = event.getEntityPlayer();
			if (player != null) {
				ItemStack held = player.getHeldItemMainhand();
				if (held.getItem() != Main.TWEEZERS_ITEM) {
					held = player.getHeldItemOffhand();
				}
				if (held.getItem() == Main.TWEEZERS_ITEM) {
					if (handleWorldTweezersClick(player)) {
						event.setCanceled(true);
					}
				}
			}
			handleLeftClick(player);
		}
	}

	public static boolean handleWorldTweezersClick(EntityPlayer player) {
		if (player == null || !player.world.isRemote)
			return false;

		// Find any EntityTick near crosshair with generous tolerance (0.8 blocks)
		EntityTick targetTick = findTickNearCrosshair(player, 4.5D, 0.8D);
		if (targetTick == null && Minecraft.getMinecraft().objectMouseOver != null) {
			Entity hitEntity = Minecraft.getMinecraft().objectMouseOver.entityHit;
			if (hitEntity != null) {
				if (hitEntity instanceof EntityTick) {
					targetTick = (EntityTick) hitEntity;
				} else {
					for (Entity passenger : hitEntity.getPassengers()) {
						if (passenger instanceof EntityTick) {
							targetTick = (EntityTick) passenger;
							break;
						}
					}
				}
			}
		}

		if (targetTick != null) {
			player.world.playSound(player.posX, player.posY, player.posZ, Main.TEAR_SOUND,
					SoundCategory.PLAYERS, 1.0F, 1.0F, false);
			player.swingArm(player.getHeldItemMainhand().getItem() == Main.TWEEZERS_ITEM ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
			Network.INSTANCE.sendToServer(new PluckMobTickMessage(targetTick.getEntityId()));
			return true;
		}
		return false;
	}

	public static EntityTick findTickNearCrosshair(EntityPlayer player, double reach, double tolerance) {
		Vec3d eyes = player.getPositionEyes(1.0F);
		Vec3d look = player.getLook(1.0F);
		Vec3d end = eyes.add(look.x * reach, look.y * reach, look.z * reach);

		List<EntityTick> ticks = player.world.getEntitiesWithinAABB(
				EntityTick.class, player.getEntityBoundingBox().grow(reach, reach, reach));
		EntityTick bestTick = null;
		double bestDistSq = Double.MAX_VALUE;

		for (EntityTick tick : ticks) {
			if (!tick.isEntityAlive())
				continue;
			AxisAlignedBB aabb = tick.getEntityBoundingBox().grow(tolerance);
			RayTraceResult intercept = aabb.calculateIntercept(eyes, end);
			if (intercept != null) {
				double distSq = eyes.squareDistanceTo(intercept.hitVec);
				if (distSq < bestDistSq) {
					bestDistSq = distSq;
					bestTick = tick;
				}
			} else {
				Vec3d tickCenter = new Vec3d(tick.posX, tick.posY + tick.height / 2.0D, tick.posZ);
				Vec3d toTick = tickCenter.subtract(eyes);
				double proj = toTick.dotProduct(look);
				if (proj >= 0.0D && proj <= reach) {
					Vec3d closestPointOnRay = eyes.add(look.x * proj, look.y * proj, look.z * proj);
					double distToRaySq = tickCenter.squareDistanceTo(closestPointOnRay);
					if (distToRaySq <= tolerance * tolerance) {
						double distSq = eyes.squareDistanceTo(tickCenter);
						if (distSq < bestDistSq) {
							bestDistSq = distSq;
							bestTick = tick;
						}
					}
				}
			}
		}
		return bestTick;
	}

	private static void handleLeftClick(EntityPlayer player) {
		if (player == null || !player.world.isRemote)
			return;

		ItemStack held = player.getHeldItemMainhand();
		if (player.isHandActive() && player.getActiveItemStack().getItem() == Main.TWEEZERS_ITEM) {
			if (player instanceof EntityPlayerSP) {
				handleTweezersClick((EntityPlayerSP) player);
			}
		} else if (held.getItem() == Main.SWATTER_ITEM) {
			// Swat attempt with swatter
			player.swingArm(EnumHand.MAIN_HAND);
			trySwatMosquito(player);
		} else if (held.isEmpty() && player.getHeldItemOffhand().isEmpty() && hasMosquitoes(player)) {
			// Wave hands
			Mosquitoes.getMosquitoes(player).waveHands();
		}
	}

	public static boolean handleTweezersClick(EntityPlayerSP player) {
		if (player.ticksExisted == lastTweezersClickTime)
			return false;
		lastTweezersClickTime = player.ticksExisted;

		tweezersPinchTicks = 4;

		Ticks ticks = Ticks.getTicks(player);
		if (!ticks.hasTick()) {
			if (handleWorldTweezersClick(player)) {
				return true;
			}
			player.playSound(SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, 0.8f, 1.8f);
			player.swingArm(player.getActiveHand());
			return false;
		}

		Tick tick = ticks.getTick();
		ItemStack stack = player.getActiveItemStack();
		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		int maxDuration = stack.getMaxItemUseDuration();
		float duration = (float) maxDuration - ((float) player.getItemInUseCount() - partialTicks + 1.0f);
		TweezersItem.Vec2f pos = TweezersItem.getPosition(duration);
		TweezersItem.Vec2f tickPos = new TweezersItem.Vec2f(tick.getX(partialTicks), tick.getY(partialTicks));

		if (TweezersItem.isOverTick(pos, tickPos)) {
			// HIT!
			player.world.playSound(player.posX, player.posY, player.posZ, Main.TEAR_SOUND,
					SoundCategory.PLAYERS, 1.0f, 1.0f, false);
			player.swingArm(player.getActiveHand());

			// Remove client-side immediately
			ClientOnlyMethods.removeTick();

			// Notify server to remove tick, damage tweezers, and broadcast
			Network.INSTANCE.sendToServer(new PluckTickMessage());

			player.resetActiveHand();
			return true;
		} else {
			// MISS: metallic snip feedback
			player.playSound(SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, 0.8f, 1.8f);
			player.swingArm(player.getActiveHand());
			return false;
		}
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		if (tweezersPinchTicks > 0) {
			tweezersPinchTicks--;
		}
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null)
			return;

		if (player.isDead || player.getHealth() <= 0.0F) {
			Ticks.getTicks(player).remove();
			return;
		}

		// When continuously holding right-click with the swatter, check swat mosquitoes
		if (player.isHandActive() && player.getActiveItemStack().getItem() == Main.SWATTER_ITEM) {
			trySwatMosquito(player);
		}
	}

	private static boolean trySwatMosquito(EntityPlayer player) {
		Mosquitoes mosquitoes = Mosquitoes.getMosquitoes(player);
		for (int i = 0; i < mosquitoes.count(); i++) {
			Mosquito m = mosquitoes.get(i);
			// Swat if aimed near or if mosquito came near/landed
			if (m.isHit(0.0f, 0.0f) || !m.isFlying()) {
				Network.INSTANCE.sendToServer(new SwatMosquitoMessage(m.getId()));
				ClientOnlyMethods.attackMosquito(m.getId(), true);
				return true;
			}
		}
		return false;
	}

	private static boolean hasMosquitoes(EntityPlayer player) {
		return Mosquitoes.getMosquitoes(player).count() > 0;
	}
}
