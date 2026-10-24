package mod.vemerion.mosquitoes.network;

import io.netty.buffer.ByteBuf;
import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SwatMosquitoMessage implements IMessage {
	private int id;

	public SwatMosquitoMessage() {
	}

	public SwatMosquitoMessage(int id) {
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
	}

	public static class Handler implements IMessageHandler<SwatMosquitoMessage, IMessage> {
		@Override
		public IMessage onMessage(SwatMosquitoMessage message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				Mosquitoes mosquitoes = Mosquitoes.getMosquitoes(player);
				if (mosquitoes.killMosquito(player, message.id)) {
					ItemStack held = player.getHeldItemMainhand();
					if (held.getItem() == Main.SWATTER_ITEM) {
						held.damageItem(1, player);
					}
					if (player.getRNG().nextDouble() < 0.1) {
						EntityItem wing = new EntityItem(player.world, player.posX, player.posY + 1.0, player.posZ,
								new ItemStack(Main.MOSQUITO_WING_ITEM));
						player.world.spawnEntity(wing);
					}
				}
			});
			return null;
		}
	}
}
