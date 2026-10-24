package mod.vemerion.mosquitoes.block;

import java.util.Random;

import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

public class CitronellaGenerator implements IWorldGenerator {

	public static boolean isCitronellaBiome(Biome biome) {
		if (biome == null)
			return false;
		if (Mosquitoes.isColdBiome(biome))
			return false;
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)) {
			return false;
		}

		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SAVANNA)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.MESA)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.DEAD)
				|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.WASTELAND)) {
			return true;
		}

		String name = biome.getBiomeName() != null ? biome.getBiomeName().toLowerCase() : "";
		ResourceLocation regName = biome.getRegistryName();
		String path = regName != null ? regName.toString().toLowerCase() : "";

		return name.contains("desert") || name.contains("mesa") || name.contains("badland")
				|| name.contains("savanna") || name.contains("dune") || name.contains("wasteland")
				|| path.contains("desert") || path.contains("mesa") || path.contains("badland")
				|| path.contains("savanna") || path.contains("dune") || path.contains("wasteland");
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
		if (world.provider.getDimension() == 0
				&& world.provider.getDimensionType() == net.minecraft.world.DimensionType.OVERWORLD) {
			int centerX = chunkX * 16 + 8;
			int centerZ = chunkZ * 16 + 8;
			Biome biome = world.getBiome(new BlockPos(centerX, 0, centerZ));

			if (isCitronellaBiome(biome)) {
				int patches = random.nextInt(2) + 1; // 1 to 2 clusters per chunk
				for (int p = 0; p < patches; p++) {
					int x = chunkX * 16 + random.nextInt(16) + 8;
					int z = chunkZ * 16 + random.nextInt(16) + 8;
					BlockPos pos = world.getHeight(new BlockPos(x, 0, z));

					for (int i = 0; i < 8; i++) {
						int px = pos.getX() + random.nextInt(8) - random.nextInt(8);
						int pz = pos.getZ() + random.nextInt(8) - random.nextInt(8);
						BlockPos plantPos = world.getHeight(new BlockPos(px, 0, pz));

						if (world.isAirBlock(plantPos)
								&& Main.CITRONELLA_BLOCK.canBlockStay(world, plantPos, Main.CITRONELLA_BLOCK.getDefaultState())) {
							world.setBlockState(plantPos, Main.CITRONELLA_BLOCK.getDefaultState(), 2);
						}
					}
				}
			}
		}
	}
}
