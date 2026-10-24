package mod.vemerion.mosquitoes.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.oredict.OreDictionary;

public class VegetationHelper {

	/**
	 * Identifies whether a block is vegetation (grass, flowers, crops, leaves/trees, bushes).
	 * Explicitly excludes bare ground blocks like grass blocks, dirt, mycelium.
	 * Has universal mod support for Biomes O' Plenty, Dynamic Trees, Pam's HarvestCraft, Botania, etc.
	 */
	public static boolean isVegetation(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.AIR) {
			return false;
		}

		// Explicitly EXCLUDE bare ground blocks (vanilla and modded: grass block, dirt, mycelium, paths)
		Material blockMat = state.getMaterial();
		if (block instanceof net.minecraft.block.BlockGrass
				|| block instanceof net.minecraft.block.BlockDirt
				|| block instanceof net.minecraft.block.BlockMycelium
				|| block instanceof net.minecraft.block.BlockGrassPath
				|| blockMat == Material.GRASS
				|| blockMat == Material.GROUND) {
			return false;
		}

		// 1. Forge IPlantable check (covers 99% of modded plants: Biomes O' Plenty, Pam's, Botania, etc.)
		if (block instanceof IPlantable) {
			return true;
		}

		// 2. Standard plant & foliage base classes
		if (block instanceof BlockBush
				|| block instanceof BlockLeaves
				|| block instanceof BlockCrops
				|| block instanceof BlockReed
				|| block instanceof BlockStem
				|| block instanceof BlockVine) {
			return true;
		}

		// 3. Material check (PLANTS, VINE, LEAVES, CACTUS)
		Material mat = state.getMaterial();
		if (mat == Material.PLANTS || mat == Material.VINE || mat == Material.LEAVES || mat == Material.CACTUS) {
			return true;
		}

		// 4. Mod-specific checks (e.g. Dynamic Trees branches/leaves, custom foliage)
		String className = block.getClass().getName().toLowerCase();
		if (className.contains("dynamictrees")
				|| className.contains("leaves")
				|| className.contains("foliage")
				|| className.contains("plant")
				|| className.contains("flower")
				|| className.contains("crop")
				|| className.contains("grass")
				|| className.contains("bush")
				|| className.contains("weed")
				|| className.contains("vine")) {
			return true;
		}

		// 5. Registry name check for modded foliage
		if (block.getRegistryName() != null) {
			String path = block.getRegistryName().getPath().toLowerCase();
			if (path.contains("grass") || path.contains("flower") || path.contains("leaf") || path.contains("leaves")
					|| path.contains("plant") || path.contains("bush") || path.contains("crop") || path.contains("sapling")
					|| path.contains("weed") || path.contains("vine") || path.contains("clover") || path.contains("fern")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Counts how many vegetation blocks (grass, flowers, crops, trees/leaves, bushes)
	 * are in the specified box around center.
	 */
	public static int countNearbyVegetation(World world, BlockPos center, int radiusXZ, int radiusY) {
		int count = 0;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int x = -radiusXZ; x <= radiusXZ; x++) {
			for (int z = -radiusXZ; z <= radiusXZ; z++) {
				for (int y = -radiusY; y <= radiusY; y++) {
					pos.setPos(center.getX() + x, center.getY() + y, center.getZ() + z);
					if (isVegetation(world, pos)) {
						count++;
					}
				}
			}
		}
		return count;
	}

	/**
	 * Checks if an area has vegetation (grass, flowers, crops, trees/leaves, bushes).
	 * Requires at least 3 vegetation blocks within a 5x3x5 area.
	 */
	public static boolean hasLotsOfVegetation(World world, BlockPos center) {
		return countNearbyVegetation(world, center, 5, 3) >= 3;
	}
}
