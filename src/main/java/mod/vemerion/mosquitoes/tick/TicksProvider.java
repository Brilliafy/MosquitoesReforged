package mod.vemerion.mosquitoes.tick;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class TicksProvider implements ICapabilitySerializable<NBTTagCompound> {

	private final Ticks ticks = new Ticks();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == Main.TICKS_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == Main.TICKS_CAP ? Main.TICKS_CAP.cast(ticks) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return ticks.save();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		ticks.load(nbt);
	}
}
