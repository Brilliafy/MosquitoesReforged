package mod.vemerion.mosquitoes;

import mod.vemerion.mosquitoes.block.CitronellaBlock;
import mod.vemerion.mosquitoes.block.CitronellaGenerator;
import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import mod.vemerion.mosquitoes.mosquito.MosquitoesStorage;
import mod.vemerion.mosquitoes.network.Network;
import mod.vemerion.mosquitoes.proxy.CommonProxy;
import mod.vemerion.mosquitoes.tick.Ticks;
import mod.vemerion.mosquitoes.tick.TicksStorage;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
	public static final String MODID = "mosquitoes";
	public static final String NAME = "Mosquitoes";
	public static final String VERSION = "1.0.0";

	@Instance(MODID)
	public static Main instance;

	@SidedProxy(clientSide = "mod.vemerion.mosquitoes.proxy.ClientProxy", serverSide = "mod.vemerion.mosquitoes.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static final net.minecraft.creativetab.CreativeTabs TAB_MOSQUITOES = new net.minecraft.creativetab.CreativeTabs(MODID) {
		@Override
		public net.minecraft.item.ItemStack createIcon() {
			return new net.minecraft.item.ItemStack(MOSQUITO_WING_ITEM != null ? MOSQUITO_WING_ITEM : net.minecraft.init.Items.FEATHER);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void displayAllRelevantItems(NonNullList<net.minecraft.item.ItemStack> items) {
			super.displayAllRelevantItems(items);
			net.minecraft.item.ItemStack mosquitoEgg = new net.minecraft.item.ItemStack(net.minecraft.init.Items.SPAWN_EGG);
			ItemMonsterPlacer.applyEntityIdToItemStack(mosquitoEgg, new ResourceLocation(MODID, "mosquito"));
			items.add(mosquitoEgg);

			net.minecraft.item.ItemStack tickEgg = new net.minecraft.item.ItemStack(net.minecraft.init.Items.SPAWN_EGG);
			ItemMonsterPlacer.applyEntityIdToItemStack(tickEgg, new ResourceLocation(MODID, "tick"));
			items.add(tickEgg);

			if (CITRONELLA_POTION != null) {
				net.minecraft.item.ItemStack pot = new net.minecraft.item.ItemStack(net.minecraft.init.Items.POTIONITEM);
				PotionUtils.addPotionToItemStack(pot, CITRONELLA_POTION);
				items.add(pot);
			}
			if (LONG_CITRONELLA_POTION != null) {
				net.minecraft.item.ItemStack pot = new net.minecraft.item.ItemStack(net.minecraft.init.Items.POTIONITEM);
				PotionUtils.addPotionToItemStack(pot, LONG_CITRONELLA_POTION);
				items.add(pot);
			}
			if (MALARIA_CURE_POTION != null) {
				net.minecraft.item.ItemStack pot = new net.minecraft.item.ItemStack(net.minecraft.init.Items.POTIONITEM);
				PotionUtils.addPotionToItemStack(pot, MALARIA_CURE_POTION);
				items.add(pot);
			}
		}
	};

	public static Item SWATTER_ITEM;
	public static Item MOSQUITO_WING_ITEM;
	public static Item TWEEZERS_ITEM;
	public static Item CITRONELLA_BLOCK_ITEM;

	public static CitronellaBlock CITRONELLA_BLOCK;

	public static Potion MALARIA_EFFECT;
	public static Potion MALARIA_CURE_EFFECT;
	public static Potion CITRONELLA_EFFECT;

	public static PotionType MALARIA_CURE_POTION;
	public static PotionType CITRONELLA_POTION;
	public static PotionType LONG_CITRONELLA_POTION;

	public static SoundEvent FLYING_MOSQUITO_SOUND;
	public static SoundEvent GULP_SOUND;
	public static SoundEvent SMACK_SOUND;
	public static SoundEvent SPLASH_SOUND;
	public static SoundEvent TEAR_SOUND;
	public static SoundEvent[] FLYING_MOSQUITO_SOUNDS = new SoundEvent[4];

	public static int getNextRandomBuzzIndex(java.util.Random rand, int currentIndex) {
		if (FLYING_MOSQUITO_SOUNDS == null || FLYING_MOSQUITO_SOUNDS.length <= 1) {
			return 0;
		}
		int next = rand.nextInt(FLYING_MOSQUITO_SOUNDS.length - 1);
		if (next >= currentIndex) {
			next++;
		}
		return next;
	}

	@CapabilityInject(Mosquitoes.class)
	public static Capability<Mosquitoes> MOSQUITOES_CAP = null;

	@CapabilityInject(Ticks.class)
	public static Capability<Ticks> TICKS_CAP = null;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(Mosquitoes.class, new MosquitoesStorage(), Mosquitoes::new);
		CapabilityManager.INSTANCE.register(Ticks.class, new TicksStorage(), Ticks::new);
		Network.init();
		GameRegistry.registerWorldGenerator(new CitronellaGenerator(), 10);
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		ModEventSubscriber.addPotionRecipes();

		java.util.List<net.minecraft.world.biome.Biome> swampBiomes = new java.util.ArrayList<>();
		java.util.List<net.minecraft.world.biome.Biome> waterBiomes = new java.util.ArrayList<>();
		java.util.List<net.minecraft.world.biome.Biome> normalBiomes = new java.util.ArrayList<>();

		for (net.minecraft.world.biome.Biome biome : net.minecraftforge.fml.common.registry.ForgeRegistries.BIOMES) {
			if (Mosquitoes.isBiomeAllowed(biome)) {
				if (Mosquitoes.isSwamp(biome)) {
					swampBiomes.add(biome);
				} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WATER)) {
					waterBiomes.add(biome);
				} else {
					normalBiomes.add(biome);
				}
			}
		}

		// Register placement types (both ON_GROUND so WorldEntitySpawner checks solid surface below and spawns entity in air block above)
		net.minecraft.entity.EntitySpawnPlacementRegistry.setPlacementType(
				mod.vemerion.mosquitoes.entity.EntityMosquito.class, net.minecraft.entity.EntityLiving.SpawnPlacementType.ON_GROUND);
		net.minecraft.entity.EntitySpawnPlacementRegistry.setPlacementType(
				mod.vemerion.mosquitoes.entity.EntityTick.class, net.minecraft.entity.EntityLiving.SpawnPlacementType.ON_GROUND);

		// Swamps: high spawn weight for mosquitoes (lots of mosquitoes at night and near water!)
		if (!swampBiomes.isEmpty()) {
			net.minecraft.world.biome.Biome[] swampArray = swampBiomes.toArray(new net.minecraft.world.biome.Biome[0]);
			net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(
					mod.vemerion.mosquitoes.entity.EntityMosquito.class,
					65, 2, 4, net.minecraft.entity.EnumCreatureType.MONSTER, swampArray);
			net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(
					mod.vemerion.mosquitoes.entity.EntityMosquito.class,
					30, 1, 3, net.minecraft.entity.EnumCreatureType.AMBIENT, swampArray);
		}
		// River / water biomes: moderate spawn weight
		if (!waterBiomes.isEmpty()) {
			net.minecraft.world.biome.Biome[] waterArray = waterBiomes.toArray(new net.minecraft.world.biome.Biome[0]);
			net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(
					mod.vemerion.mosquitoes.entity.EntityMosquito.class,
					35, 1, 3, net.minecraft.entity.EnumCreatureType.MONSTER, waterArray);
			net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(
					mod.vemerion.mosquitoes.entity.EntityMosquito.class,
					15, 1, 2, net.minecraft.entity.EnumCreatureType.AMBIENT, waterArray);
		}
		// Normal surface biomes: rare spawn weight
		if (!normalBiomes.isEmpty()) {
			net.minecraft.world.biome.Biome[] normalArray = normalBiomes.toArray(new net.minecraft.world.biome.Biome[0]);
			net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(
					mod.vemerion.mosquitoes.entity.EntityMosquito.class,
					12, 1, 2, net.minecraft.entity.EnumCreatureType.MONSTER, normalArray);
			net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(
					mod.vemerion.mosquitoes.entity.EntityMosquito.class,
					4, 1, 1, net.minecraft.entity.EnumCreatureType.AMBIENT, normalArray);
		}

		// Ticks: natural ambient spawning near vegetation (weight 10, 1 per pack, strictly density capped)
		java.util.List<net.minecraft.world.biome.Biome> allBiomes = new java.util.ArrayList<>();
		allBiomes.addAll(swampBiomes);
		allBiomes.addAll(waterBiomes);
		allBiomes.addAll(normalBiomes);
		if (!allBiomes.isEmpty()) {
			net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(
					mod.vemerion.mosquitoes.entity.EntityTick.class,
					10, 1, 1, net.minecraft.entity.EnumCreatureType.AMBIENT,
					allBiomes.toArray(new net.minecraft.world.biome.Biome[0]));
		}

		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
