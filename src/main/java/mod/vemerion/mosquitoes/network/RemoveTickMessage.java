package mod.vemerion.mosquitoes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoveTickMessage implements IMessage {

	public RemoveTickMessage() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	public static class Handler implements IMessageHandler<RemoveTickMessage, IMessage> {
		@Override
		public IMessage onMessage(RemoveTickMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ClientOnlyMethods.removeTick();
			});
			return null;
		}
	}
}
