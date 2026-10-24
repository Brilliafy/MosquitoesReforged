package mod.vemerion.mosquitoes.network;

import mod.vemerion.mosquitoes.Main;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Network {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Main.MODID);
	private static int id = 0;

	public static void init() {
		INSTANCE.registerMessage(SpawnMosquitoesMessage.Handler.class, SpawnMosquitoesMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(WavingMessage.Handler.class, WavingMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(AttackMosquitoMessage.Handler.class, AttackMosquitoMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(SynchMosquitoesMessage.Handler.class, SynchMosquitoesMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(SynchTicksMessage.Handler.class, SynchTicksMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(RemoveTickMessage.Handler.class, RemoveTickMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(SwatMosquitoMessage.Handler.class, SwatMosquitoMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(PluckTickMessage.Handler.class, PluckTickMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(PluckMobTickMessage.Handler.class, PluckMobTickMessage.class, id++, Side.SERVER);
	}
}
