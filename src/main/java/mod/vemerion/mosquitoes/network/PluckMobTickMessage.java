package mod.vemerion.mosquitoes.network;

import io.netty.buffer.ByteBuf;
import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.entity.EntityTick;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PluckMobTickMessage implements IMessage {
	private int tickId;

	public PluckMobTickMessage() {
	}

	public PluckMobTickMessage(int tickId) {
		this.tickId = tickId;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.tickId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(tickId);
	}

	public static class Handler implements IMessageHandler<PluckMobTickMessage, IMessage> {
		@Override
		public IMessage onMessage(PluckMobTickMessage message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				Entity entity = player.world.getEntityByID(message.tickId);
				if (entity instanceof EntityTick && player.getDistanceSq(entity) <= 36.0D) {
					EntityTick tick = (EntityTick) entity;
					ItemStack held = player.getHeldItemMainhand();
					if (held.getItem() != Main.TWEEZERS_ITEM) {
						held = player.getHeldItemOffhand();
					}
					if (held.getItem() == Main.TWEEZERS_ITEM) {
						held.damageItem(1, player);
						player.world.playSound(null, tick.posX, tick.posY, tick.posZ, Main.TEAR_SOUND,
								SoundCategory.PLAYERS, 1.0F, 1.0F);

						if (tick.isRiding()) {
							tick.dismountRidingEntity();
							tick.motionY = 0.25D;
							tick.motionX = (player.posX - tick.posX) * -0.1D;
							tick.motionZ = (player.posZ - tick.posZ) * -0.1D;
						}

						tick.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F);
					}
				}
			});
			return null;
		}
	}
}
