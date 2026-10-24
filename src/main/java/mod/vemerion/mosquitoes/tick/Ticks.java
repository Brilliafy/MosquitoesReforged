package mod.vemerion.mosquitoes.tick;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import mod.vemerion.mosquitoes.network.Network;
import mod.vemerion.mosquitoes.network.RemoveTickMessage;
import mod.vemerion.mosquitoes.network.SynchTicksMessage;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class Ticks {

	private static final Set<Block> SPAWN_POSITIONS = ImmutableSet.of(Blocks.TALLGRASS, Blocks.GRASS);
	private static final int MAX_SPAWN_TIMER = 20 * 60 * 15;

	private Tick tick;
	private int spawnTimer;

	public Ticks() {
		tick = null;
		spawnTimer = MAX_SPAWN_TIMER;
	}

	public void tick(EntityPlayer player) {
		if (tick != null) {
			tick.tick(player);
		}

		if (!player.world.isRemote) {
			// Overworld only (no Nether/End/modded dims), surface only (NOT IN CAVES!), non-cold biome, and lots of vegetation
			if (player.world.provider.getDimension() == 0
					&& player.world.provider.getDimensionType() == net.minecraft.world.DimensionType.OVERWORLD
					&& Mosquitoes.isSurface(player)
					&& inDenseVegetation(player)
					&& tick == null) {
				BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
				net.minecraft.world.biome.Biome biome = player.world.getBiome(pos);
				if (Mosquitoes.isBiomeAllowed(biome) && !Mosquitoes.isColdBiome(biome)) {
					spawnTimer--;
					if (spawnTimer < 0) {
						spawnTimer = MAX_SPAWN_TIMER;
						// Armor check: 1.5x less likely per armor piece worn (~5x with full armor)
						int armorCount = 0;
						for (net.minecraft.item.ItemStack armor : player.inventory.armorInventory) {
							if (!armor.isEmpty()) {
								armorCount++;
							}
						}
						double attachChance = 1.0D / Math.pow(1.5D, armorCount);
						if (player.getRNG().nextDouble() < attachChance) {
							createTick(player);
						}
					}
				}
			}
		}
	}

	public boolean hasTick() {
		return tick != null;
	}

	public Tick getTick() {
		return tick;
	}

	public NBTTagCompound save() {
		NBTTagCompound compound = new NBTTagCompound();
		if (tick != null) {
			tick.save(compound);
		}
		compound.setBoolean("hasTick", tick != null);
		compound.setInteger("spawnTimer", spawnTimer);
		return compound;
	}

	public void load(NBTTagCompound compound) {
		if (compound.getBoolean("hasTick")) {
			tick = new Tick();
			tick.load(compound);
		} else {
			tick = null;
		}
		spawnTimer = compound.getInteger("spawnTimer");
	}

	public void createTick(EntityPlayer player) {
		tick = new Tick();
		sendSyncMessage(player);
	}

	public void sendSyncMessage(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			Network.INSTANCE.sendTo(new SynchTicksMessage(save()), (EntityPlayerMP) player);
		}
	}

	public void sendRemoveMessage(EntityPlayer player) {
		if (tick != null) {
			tick = null;
			if (player instanceof EntityPlayerMP) {
				Network.INSTANCE.sendTo(new RemoveTickMessage(), (EntityPlayerMP) player);
			}
		}
	}

	private boolean inDenseVegetation(EntityPlayer player) {
		BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
		return mod.vemerion.mosquitoes.util.VegetationHelper.hasLotsOfVegetation(player.world, pos);
	}

	public static Ticks getTicks(EntityPlayer player) {
		if (player.hasCapability(Main.TICKS_CAP, null)) {
			return player.getCapability(Main.TICKS_CAP, null);
		}
		return new Ticks();
	}

	public void remove() {
		tick = null;
	}
}
