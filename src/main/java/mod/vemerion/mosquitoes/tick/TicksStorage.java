package mod.vemerion.mosquitoes.tick;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class TicksStorage implements Capability.IStorage<Ticks> {

	@Override
	public NBTBase writeNBT(Capability<Ticks> capability, Ticks instance, EnumFacing side) {
		return instance.save();
	}

	@Override
	public void readNBT(Capability<Ticks> capability, Ticks instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			instance.load((NBTTagCompound) nbt);
		}
	}
}
