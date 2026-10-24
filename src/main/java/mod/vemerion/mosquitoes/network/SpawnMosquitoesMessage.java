package mod.vemerion.mosquitoes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SpawnMosquitoesMessage implements IMessage {
	private int count;

	public SpawnMosquitoesMessage() {
	}

	public SpawnMosquitoesMessage(int count) {
		this.count = count;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.count = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(count);
	}

	public static class Handler implements IMessageHandler<SpawnMosquitoesMessage, IMessage> {
		@Override
		public IMessage onMessage(SpawnMosquitoesMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ClientOnlyMethods.spawnMosquitoes(message.count);
			});
			return null;
		}
	}
}
