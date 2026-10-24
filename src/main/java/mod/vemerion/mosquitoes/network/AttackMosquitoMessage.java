package mod.vemerion.mosquitoes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AttackMosquitoMessage implements IMessage {
	private int id;
	private boolean kill;

	public AttackMosquitoMessage() {
	}

	public AttackMosquitoMessage(int id, boolean kill) {
		this.id = id;
		this.kill = kill;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.kill = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeBoolean(kill);
	}

	public static class Handler implements IMessageHandler<AttackMosquitoMessage, IMessage> {
		@Override
		public IMessage onMessage(AttackMosquitoMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ClientOnlyMethods.attackMosquito(message.id, message.kill);
			});
			return null;
		}
	}
}
