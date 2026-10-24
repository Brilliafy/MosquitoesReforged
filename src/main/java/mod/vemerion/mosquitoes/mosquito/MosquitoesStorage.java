package mod.vemerion.mosquitoes.mosquito;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class MosquitoesStorage implements Capability.IStorage<Mosquitoes> {

	@Override
	public NBTBase writeNBT(Capability<Mosquitoes> capability, Mosquitoes instance, EnumFacing side) {
		return instance.save();
	}

	@Override
	public void readNBT(Capability<Mosquitoes> capability, Mosquitoes instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			instance.load((NBTTagCompound) nbt);
		}
	}
}
