package mod.vemerion.mosquitoes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SynchMosquitoesMessage implements IMessage {
	private NBTTagCompound compound;

	public SynchMosquitoesMessage() {
	}

	public SynchMosquitoesMessage(NBTTagCompound compound) {
		this.compound = compound;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.compound = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, compound);
	}

	public static class Handler implements IMessageHandler<SynchMosquitoesMessage, IMessage> {
		@Override
		public IMessage onMessage(SynchMosquitoesMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ClientOnlyMethods.synchMosquitoes(message.compound);
			});
			return null;
		}
	}
}
