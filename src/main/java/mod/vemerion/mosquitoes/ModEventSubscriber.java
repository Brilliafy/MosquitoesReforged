package mod.vemerion.mosquitoes;

import mod.vemerion.mosquitoes.block.CitronellaBlock;
import mod.vemerion.mosquitoes.item.MosquitoWingItem;
import mod.vemerion.mosquitoes.item.SwatterItem;
import mod.vemerion.mosquitoes.item.TweezersItem;
import mod.vemerion.mosquitoes.potion.CitronellaEffect;
import mod.vemerion.mosquitoes.potion.MalariaCureEffect;
import mod.vemerion.mosquitoes.potion.MalariaEffect;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(modid = Main.MODID)
public class ModEventSubscriber {

	@SubscribeEvent
	public static void onRegisterBlock(RegistryEvent.Register<Block> event) {
		Main.CITRONELLA_BLOCK = setup(new CitronellaBlock(), "citronella_block");
		event.getRegistry().register(Main.CITRONELLA_BLOCK);
	}

	@SubscribeEvent
	public static void onRegisterItem(RegistryEvent.Register<Item> event) {
		Main.SWATTER_ITEM = setup(new SwatterItem(), "swatter_item");
		Main.MOSQUITO_WING_ITEM = setup(new MosquitoWingItem(), "mosquito_wing_item");
		Main.TWEEZERS_ITEM = setup(new TweezersItem(), "tweezers_item");
		Main.CITRONELLA_BLOCK_ITEM = setup(new ItemBlock(Main.CITRONELLA_BLOCK), "citronella_block");
		Main.CITRONELLA_BLOCK_ITEM.setCreativeTab(Main.TAB_MOSQUITOES);

		event.getRegistry().register(Main.SWATTER_ITEM);
		event.getRegistry().register(Main.MOSQUITO_WING_ITEM);
		event.getRegistry().register(Main.TWEEZERS_ITEM);
		event.getRegistry().register(Main.CITRONELLA_BLOCK_ITEM);
	}

	@SubscribeEvent
	public static void onRegisterEntity(RegistryEvent.Register<net.minecraftforge.fml.common.registry.EntityEntry> event) {
		net.minecraftforge.fml.common.registry.EntityEntry mosquitoEntry = net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
				.entity(mod.vemerion.mosquitoes.entity.EntityMosquito.class)
				.id(new ResourceLocation(Main.MODID, "mosquito"), 1)
				.name("mosquito")
				.tracker(64, 3, true)
				.egg(0x2B1810, 0x8B0000)
				.build();

		net.minecraftforge.fml.common.registry.EntityEntry tickEntry = net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
				.entity(mod.vemerion.mosquitoes.entity.EntityTick.class)
				.id(new ResourceLocation(Main.MODID, "tick"), 2)
				.name("tick")
				.tracker(64, 3, true)
				.egg(0x1B1008, 0x5C2E0B)
				.build();

		event.getRegistry().register(mosquitoEntry);
		event.getRegistry().register(tickEntry);
	}

	@SubscribeEvent
	public static void onRegisterPotion(RegistryEvent.Register<Potion> event) {
		Main.MALARIA_EFFECT = setup(new MalariaEffect(), "malaria_effect");
		Main.MALARIA_CURE_EFFECT = setup(new MalariaCureEffect(), "malaria_cure_effect");
		Main.CITRONELLA_EFFECT = setup(new CitronellaEffect(), "citronella_effect");

		event.getRegistry().register(Main.MALARIA_EFFECT);
		event.getRegistry().register(Main.MALARIA_CURE_EFFECT);
		event.getRegistry().register(Main.CITRONELLA_EFFECT);
	}

	@SubscribeEvent
	public static void onRegisterPotionType(RegistryEvent.Register<PotionType> event) {
		Main.MALARIA_CURE_POTION = setup(new PotionType("malaria_cure_potion",
				new PotionEffect(Main.MALARIA_CURE_EFFECT, 1)), "malaria_cure_potion");
		Main.CITRONELLA_POTION = setup(new PotionType("citronella_potion",
				new PotionEffect(Main.CITRONELLA_EFFECT, 20 * 60 * 20)), "citronella_potion");
		Main.LONG_CITRONELLA_POTION = setup(new PotionType("long_citronella_potion",
				new PotionEffect(Main.CITRONELLA_EFFECT, 20 * 60 * 20 * 2)), "long_citronella_potion");

		event.getRegistry().register(Main.MALARIA_CURE_POTION);
		event.getRegistry().register(Main.CITRONELLA_POTION);
		event.getRegistry().register(Main.LONG_CITRONELLA_POTION);
	}

	public static void addPotionRecipes() {
		PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromItem(Main.MOSQUITO_WING_ITEM), Main.MALARIA_CURE_POTION);
		PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromItem(Main.CITRONELLA_BLOCK_ITEM), Main.CITRONELLA_POTION);
		PotionHelper.addMix(Main.CITRONELLA_POTION, Ingredient.fromItem(Items.REDSTONE), Main.LONG_CITRONELLA_POTION);
	}

	@SubscribeEvent
	public static void onRegisterSound(RegistryEvent.Register<SoundEvent> event) {
		Main.FLYING_MOSQUITO_SOUND = setup(new SoundEvent(new ResourceLocation(Main.MODID, "flying_mosquito_sound")),
				"flying_mosquito_sound");
		for (int i = 0; i < 4; i++) {
			Main.FLYING_MOSQUITO_SOUNDS[i] = setup(new SoundEvent(new ResourceLocation(Main.MODID, "flying_mosquito_sound" + i)),
					"flying_mosquito_sound" + i);
		}
		Main.GULP_SOUND = setup(new SoundEvent(new ResourceLocation(Main.MODID, "gulp_sound")), "gulp_sound");
		Main.SMACK_SOUND = setup(new SoundEvent(new ResourceLocation(Main.MODID, "smack_sound")), "smack_sound");
		Main.SPLASH_SOUND = setup(new SoundEvent(new ResourceLocation(Main.MODID, "splash_sound")), "splash_sound");
		Main.TEAR_SOUND = setup(new SoundEvent(new ResourceLocation(Main.MODID, "tear_sound")), "tear_sound");

		event.getRegistry().register(Main.FLYING_MOSQUITO_SOUND);
		for (int i = 0; i < 4; i++) {
			event.getRegistry().register(Main.FLYING_MOSQUITO_SOUNDS[i]);
		}
		event.getRegistry().register(Main.GULP_SOUND);
		event.getRegistry().register(Main.SMACK_SOUND);
		event.getRegistry().register(Main.SPLASH_SOUND);
		event.getRegistry().register(Main.TEAR_SOUND);
	}

	public static <T extends IForgeRegistryEntry<?>> T setup(final T entry, final String name) {
		return setup(entry, new ResourceLocation(Main.MODID, name));
	}

	public static <T extends IForgeRegistryEntry<?>> T setup(final T entry, final ResourceLocation registryName) {
		entry.setRegistryName(registryName);
		return entry;
	}
}
