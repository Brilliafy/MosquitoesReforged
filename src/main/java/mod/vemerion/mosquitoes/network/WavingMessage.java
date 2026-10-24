package mod.vemerion.mosquitoes.network;

import io.netty.buffer.ByteBuf;
import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WavingMessage implements IMessage {

	public WavingMessage() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	public static class Handler implements IMessageHandler<WavingMessage, IMessage> {
		@Override
		public IMessage onMessage(WavingMessage message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				Mosquitoes.getMosquitoes(player).chaseAwayRandomMosquito(player);
			});
			return null;
		}
	}
}
