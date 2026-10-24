package mod.vemerion.mosquitoes.mosquito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.network.AttackMosquitoMessage;
import mod.vemerion.mosquitoes.network.Network;
import mod.vemerion.mosquitoes.network.SpawnMosquitoesMessage;
import mod.vemerion.mosquitoes.network.WavingMessage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class Mosquitoes {

	private static final int MAX_SPAWN_TIMER = 20 * 60 * 10;
	private static final int MAX_DAMAGE_COOLDOWN = 20;
	private static final DamageSource SUCKING = new DamageSource("sucking").setDamageBypassesArmor();

	private List<Mosquito> mosquitoes;
	private Random rand;
	private int spawnTimer;
	private int idCounter;
	private int waveHandsCooldown;
	private int damageCooldown;
	private int hudBuzzIndex;
	private int hudBuzzTimer;

	public Mosquitoes() {
		this.init();
	}

	private void init() {
		rand = new Random();
		mosquitoes = new ArrayList<>();
		spawnTimer = MAX_SPAWN_TIMER;
		idCounter = 0;
		waveHandsCooldown = 0;
		damageCooldown = MAX_DAMAGE_COOLDOWN;
		hudBuzzIndex = -1;
		hudBuzzTimer = 0;
	}

	public void tick(EntityPlayer player) {
		int sucking = 0;
		boolean someoneFlying = false;
		for (Mosquito m : mosquitoes) {
			m.tick(player);
			if (m.isSucking())
				sucking++;
			if (m.isFlying())
				someoneFlying = true;
		}
		waveHandsCooldown--;

		if (!player.world.isRemote) {
			damageCooldown -= sucking;
			if (damageCooldown < 0) {
				damageCooldown = MAX_DAMAGE_COOLDOWN;
				int armorCount = 0;
				for (ItemStack armor : player.inventory.armorInventory) {
					if (!armor.isEmpty()) {
						armorCount++;
					}
				}
				// 0.8x less likely per armor piece worn, max 3.2x with full armor set
				double biteChance = 1.0D / (1.0D + armorCount * 0.55D);
				if (rand.nextDouble() < biteChance) {
					player.attackEntityFrom(SUCKING, 1);
				} else {
					player.world.playSound(null, player.posX, player.posY, player.posZ,
							SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 0.6F,
							1.6F + rand.nextFloat() * 0.4F);
				}
			}

			// Spawning rules: OVERWORLD ONLY (NO NETHER/END/MODDED DIMS), SURFACE ONLY (NOT IN CAVES), and MOSTLY AT NIGHT
			if (player.world.provider.getDimension() == 0
					&& player.world.provider.getDimensionType() == net.minecraft.world.DimensionType.OVERWORLD
					&& isSurface(player)) {
				BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
				Biome biome = player.world.getBiome(pos);
				decrementSpawnTimer(player.world, biome);
				if (spawnTimer < 0) {
					spawnTimer = MAX_SPAWN_TIMER;
					int count = getSpawnCount(player.world, biome);
					if (count > 0 && !player.isPotionActive(Main.CITRONELLA_EFFECT)) {
						mosquitoArrives(count);
						Network.INSTANCE.sendTo(new SpawnMosquitoesMessage(count), (EntityPlayerMP) player);
					}
				}
			}
		} else {
			if (someoneFlying) {
				if (hudBuzzTimer <= 0) {
					hudBuzzIndex = Main.getNextRandomBuzzIndex(rand, hudBuzzIndex);
					SoundEvent sound = Main.FLYING_MOSQUITO_SOUNDS[hudBuzzIndex];
					if (sound != null) {
						player.world.playSound(player.posX, player.posY, player.posZ, sound,
								SoundCategory.HOSTILE, 0.9F, 0.96F + rand.nextFloat() * 0.08F, false);
					}
					hudBuzzTimer = 20; // 2-tick overlap crossfade before 22-tick sound ends
				}
				hudBuzzTimer--;
			} else {
				hudBuzzTimer = 0;
			}
			if (sucking > 0 && player.ticksExisted % 20 == 0) {
				player.world.playSound(player.posX, player.posY, player.posZ, Main.GULP_SOUND,
						SoundCategory.HOSTILE, 1f, 0.8f + rand.nextFloat() * 0.4f, false);
			}
		}

		for (int i = count() - 1; i >= 0; i--) {
			if (mosquitoes.get(i).hasLeft()) {
				mosquitoes.remove(i);
			}
		}
	}

	public static boolean isSurface(EntityPlayer player) {
		return isSurface(player.world, new BlockPos(player.posX, player.posY, player.posZ));
	}

	public static boolean isSurface(World world, BlockPos pos) {
		if (world.provider.isNether() || world.provider.getDimension() == 1) {
			return false;
		}
		boolean canSeeSky = world.canSeeSky(pos) || world.canSeeSky(pos.up());
		int topY = world.getHeight(pos.getX(), pos.getZ());
		// Strict cave detection: underground far below surface and no skylight
		if (!canSeeSky && (pos.getY() < topY - 5) && (world.getLightFor(EnumSkyBlock.SKY, pos) < 4)) {
			return false;
		}
		return canSeeSky || pos.getY() >= topY - 5 || world.getLightFor(EnumSkyBlock.SKY, pos) >= 4;
	}

	public static boolean isNightTime(World world) {
		return !world.isDaytime() || (world.getWorldTime() % 24000L >= 12500L);
	}

	public static boolean isColdBiome(Biome biome) {
		if (biome == null)
			return false;
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)) {
			return true;
		}
		if (biome.isSnowyBiome() || biome.getEnableSnow() || biome.getDefaultTemperature() < 0.3F) {
			return true;
		}
		String name = biome.getBiomeName() != null ? biome.getBiomeName().toLowerCase() : "";
		ResourceLocation regName = biome.getRegistryName();
		String path = regName != null ? regName.toString().toLowerCase() : "";
		return name.contains("cold") || name.contains("snow") || name.contains("ice")
				|| name.contains("frozen") || name.contains("frost") || name.contains("glacier")
				|| name.contains("tundra") || name.contains("arctic") || name.contains("polar")
				|| name.contains("alps") || name.contains("winter") || name.contains("boreal")
				|| path.contains("cold") || path.contains("snow") || path.contains("ice")
				|| path.contains("frozen") || path.contains("frost") || path.contains("glacier")
				|| path.contains("tundra") || path.contains("arctic") || path.contains("polar")
				|| path.contains("alps") || path.contains("winter") || path.contains("boreal");
	}

	public static boolean isBiomeAllowed(Biome biome) {
		if (biome == null)
			return false;
		if (isColdBiome(biome)) {
			return false;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.VOID)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)) {
			return false;
		}
		ResourceLocation regName = biome.getRegistryName();
		String path = regName != null ? regName.toString().toLowerCase() : "";
		String name = biome.getBiomeName() != null ? biome.getBiomeName().toLowerCase() : "";
		if (path.contains("nether") || path.contains("end") || path.contains("twilight")
				|| path.contains("aether") || path.contains("space") || path.contains("galacticraft")
				|| path.contains("betweenlands") || path.contains("deepdark") || path.contains("abyssalcraft")
				|| name.contains("nether") || name.contains("the end") || name.contains("twilight")) {
			return false;
		}
		return true;
	}

	public static boolean isSwamp(Biome biome) {
		if (biome == null)
			return false;
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) {
			return true;
		}
		String name = biome.getBiomeName().toLowerCase();
		ResourceLocation regName = biome.getRegistryName();
		String path = regName != null ? regName.toString().toLowerCase() : "";
		return name.contains("swamp") || name.contains("marsh") || name.contains("bog")
				|| name.contains("bayou") || name.contains("wetland") || name.contains("fen")
				|| name.contains("moor") || name.contains("mire") || name.contains("slough")
				|| path.contains("swamp") || path.contains("marsh") || path.contains("bog")
				|| path.contains("bayou") || path.contains("wetland") || path.contains("fen")
				|| path.contains("moor") || path.contains("mire") || path.contains("slough");
	}

	public static boolean isNearWater(World world, BlockPos pos, int radius) {
		int minX = pos.getX() - radius;
		int maxX = pos.getX() + radius;
		int minZ = pos.getZ() - radius;
		int maxZ = pos.getZ() + radius;
		int minY = Math.max(1, pos.getY() - 3);
		int maxY = Math.min(world.getHeight() - 1, pos.getY() + 3);

		for (int x = minX; x <= maxX; x += 2) {
			for (int z = minZ; z <= maxZ; z += 2) {
				for (int y = minY; y <= maxY; y++) {
					BlockPos check = new BlockPos(x, y, z);
					if (world.getBlockState(check).getMaterial() == net.minecraft.block.material.Material.WATER) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void decrementSpawnTimer(World world, Biome biome) {
		if (!isBiomeAllowed(biome))
			return;

		boolean night = isNightTime(world);
		boolean wet = BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP);

		if (night) {
			// Highly active at night
			if (wet) {
				spawnTimer -= rand.nextInt(3) + 2;
			} else {
				spawnTimer -= rand.nextInt(2) + 1;
			}
		} else {
			// Very, very rarely in the day (rarely decrements spawn timer)
			if (rand.nextInt(15) == 0) {
				spawnTimer--;
			}
		}
	}

	public int getSpawnCount(World world, Biome biome) {
		if (!isBiomeAllowed(biome))
			return 0;

		boolean night = isNightTime(world);
		boolean wet = BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP);

		if (night) {
			if (wet) {
				return rand.nextInt(4) + 2;
			} else {
				return rand.nextInt(3) + 1;
			}
		} else {
			// Day: very few, rarely spawn - might not see them in the day
			if (rand.nextDouble() < 0.20) {
				return 1;
			}
			return 0;
		}
	}

	public boolean killMosquito(EntityPlayer player, int id) {
		for (int i = 0; i < count(); i++) {
			if (get(i).getId() == id) {
				mosquitoes.remove(i);
				if (player instanceof EntityPlayerMP) {
					Network.INSTANCE.sendTo(new AttackMosquitoMessage(id, true), (EntityPlayerMP) player);
				}
				return true;
			}
		}
		return false;
	}

	public boolean killRandomMosquito(EntityPlayer player) {
		Mosquito removed = null;
		if (!mosquitoes.isEmpty()) {
			removed = mosquitoes.remove(rand.nextInt(count()));
		}

		if (removed == null)
			return false;

		if (rand.nextDouble() < 0.1) {
			EntityItem wing = new EntityItem(player.world, player.posX, player.posY + 1.0, player.posZ,
					new ItemStack(Main.MOSQUITO_WING_ITEM));
			player.world.spawnEntity(wing);
		}

		if (player instanceof EntityPlayerMP) {
			Network.INSTANCE.sendTo(new AttackMosquitoMessage(removed.getId(), true), (EntityPlayerMP) player);
		}
		return true;
	}

	public boolean chaseAwayRandomMosquito(EntityPlayer player) {
		if (waveHandsCooldown < 0) {
			waveHandsCooldown = 20;
			if (!mosquitoes.isEmpty() && rand.nextDouble() < 0.5) {
				Mosquito chasedAway = mosquitoes.get(rand.nextInt(count()));
				chasedAway.chaseAway();
				if (player instanceof EntityPlayerMP) {
					Network.INSTANCE.sendTo(new AttackMosquitoMessage(chasedAway.getId(), false), (EntityPlayerMP) player);
				}
				return true;
			}
		}
		return false;
	}

	public void citronellaEffect(EntityPlayer player) {
		for (Mosquito m : mosquitoes) {
			m.chaseAway();
			if (player instanceof EntityPlayerMP) {
				Network.INSTANCE.sendTo(new AttackMosquitoMessage(m.getId(), false), (EntityPlayerMP) player);
			}
		}
	}

	public boolean killMosquitoClient(int id) {
		for (int i = 0; i < count(); i++) {
			if (get(i).getId() == id) {
				mosquitoes.remove(i);
				return true;
			}
		}
		return false;
	}

	public boolean chaseAwayMosquitoClient(int id) {
		for (int i = 0; i < count(); i++) {
			if (get(i).getId() == id) {
				get(i).chaseAway();
				return true;
			}
		}
		return false;
	}

	public void waveHands() {
		if (waveHandsCooldown < 0) {
			waveHandsCooldown = 20;
			Network.INSTANCE.sendToServer(new WavingMessage());
		}
	}

	public void mosquitoArrives(int count) {
		for (int i = 0; i < count; i++) {
			mosquitoes.add(new Mosquito(rand, idCounter++));
		}
	}

	public int count() {
		return mosquitoes.size();
	}

	public Mosquito get(int i) {
		return mosquitoes.get(i);
	}

	public void load(NBTTagCompound compound) {
		init();
		idCounter = compound.getInteger("idCounter");
		if (compound.hasKey("spawnTimer"))
			spawnTimer = compound.getInteger("spawnTimer");
		int[] ticksExisted = compound.getIntArray("ticksExisted");
		int[] ids = compound.getIntArray("ids");
		for (int i = 0; i < ids.length; i++) {
			mosquitoes.add(new Mosquito(rand, ids[i], ticksExisted[i]));
		}
	}

	public NBTTagCompound save() {
		NBTTagCompound compound = new NBTTagCompound();
		int[] ticksExisted = new int[count()];
		int[] ids = new int[count()];
		for (int i = 0; i < ticksExisted.length; i++) {
			ticksExisted[i] = get(i).ticksExisted();
			ids[i] = get(i).getId();
		}
		compound.setIntArray("ticksExisted", ticksExisted);
		compound.setIntArray("ids", ids);
		compound.setInteger("idCounter", idCounter);
		compound.setInteger("spawnTimer", spawnTimer);
		return compound;
	}

	public static Mosquitoes getMosquitoes(EntityLivingBase entity) {
		if (entity.hasCapability(Main.MOSQUITOES_CAP, null)) {
			return entity.getCapability(Main.MOSQUITOES_CAP, null);
		}
		return new Mosquitoes();
	}
}
