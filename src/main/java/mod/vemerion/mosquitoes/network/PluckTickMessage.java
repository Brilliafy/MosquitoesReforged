package mod.vemerion.mosquitoes.network;

import io.netty.buffer.ByteBuf;
import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.tick.Ticks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PluckTickMessage implements IMessage {

	public PluckTickMessage() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	public static class Handler implements IMessageHandler<PluckTickMessage, IMessage> {
		@Override
		public IMessage onMessage(PluckTickMessage message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				Ticks ticks = Ticks.getTicks(player);
				if (ticks.hasTick()) {
					ItemStack held = player.getHeldItemMainhand();
					if (held.getItem() != Main.TWEEZERS_ITEM) {
						held = player.getHeldItemOffhand();
					}
					if (held.getItem() == Main.TWEEZERS_ITEM) {
						player.world.playSound(null, player.posX, player.posY, player.posZ, Main.TEAR_SOUND,
								SoundCategory.PLAYERS, 1.0f, 1.0f);
						ticks.sendRemoveMessage(player);
						held.damageItem(1, player);
						player.resetActiveHand();
					}
				}
			});
			return null;
		}
	}
}
