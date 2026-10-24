package mod.vemerion.mosquitoes.block;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CitronellaBlock extends BlockBush {
	public static final AxisAlignedBB SHAPE = new AxisAlignedBB(5.0D / 16.0D, 0.0D, 5.0D / 16.0D, 11.0D / 16.0D, 15.0D / 16.0D, 11.0D / 16.0D);

	public CitronellaBlock() {
		super(Material.PLANTS);
		setHardness(0.0F);
		setSoundType(SoundType.PLANT);
		setCreativeTab(Main.TAB_MOSQUITOES);
		setTranslationKey(Main.MODID + ".citronella_block");
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return SHAPE.offset(state.getOffset(source, pos));
	}

	@Override
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}

	@Override
	public boolean canBlockStay(net.minecraft.world.World worldIn, BlockPos pos, IBlockState state) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		return canSustainBush(soil);
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		Block block = state.getBlock();
		return block == net.minecraft.init.Blocks.GRASS
				|| block == net.minecraft.init.Blocks.DIRT
				|| block == net.minecraft.init.Blocks.FARMLAND
				|| block == net.minecraft.init.Blocks.SAND
				|| block == net.minecraft.init.Blocks.HARDENED_CLAY
				|| block == net.minecraft.init.Blocks.STAINED_HARDENED_CLAY;
	}
}
