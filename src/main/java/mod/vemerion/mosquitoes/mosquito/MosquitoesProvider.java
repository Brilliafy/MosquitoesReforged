package mod.vemerion.mosquitoes.mosquito;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class MosquitoesProvider implements ICapabilitySerializable<NBTTagCompound> {

	private final Mosquitoes mosquitoes = new Mosquitoes();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == Main.MOSQUITOES_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == Main.MOSQUITOES_CAP ? Main.MOSQUITOES_CAP.cast(mosquitoes) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return mosquitoes.save();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		mosquitoes.load(nbt);
	}
}
