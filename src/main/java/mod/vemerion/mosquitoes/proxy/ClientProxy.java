package mod.vemerion.mosquitoes.proxy;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import mod.vemerion.mosquitoes.client.RenderMosquito;
import mod.vemerion.mosquitoes.client.RenderTick;
import mod.vemerion.mosquitoes.entity.EntityMosquito;
import mod.vemerion.mosquitoes.entity.EntityTick;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@EventBusSubscriber(modid = Main.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		RenderingRegistry.registerEntityRenderingHandler(EntityMosquito.class, RenderMosquito::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTick.class, RenderTick::new);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		if (item != null && item.getRegistryName() != null) {
			ModelLoader.setCustomModelResourceLocation(item, meta,
					new ModelResourceLocation(item.getRegistryName(), id));
		}
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		Main.proxy.registerItemRenderer(Main.SWATTER_ITEM, 0, "inventory");
		Main.proxy.registerItemRenderer(Main.MOSQUITO_WING_ITEM, 0, "inventory");
		Main.proxy.registerItemRenderer(Main.TWEEZERS_ITEM, 0, "inventory");
		Main.proxy.registerItemRenderer(Main.CITRONELLA_BLOCK_ITEM, 0, "inventory");
	}
}
