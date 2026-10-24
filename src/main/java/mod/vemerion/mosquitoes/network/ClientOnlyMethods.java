package mod.vemerion.mosquitoes.network;

import java.util.Random;

import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import mod.vemerion.mosquitoes.particle.MosquitoParticle;
import mod.vemerion.mosquitoes.tick.Ticks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientOnlyMethods {

	public static void attackMosquito(int id, boolean kill) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null)
			return;
		Random rand = player.getRNG();
		Mosquitoes mosquitoes = Mosquitoes.getMosquitoes(player);

		if (kill && mosquitoes.killMosquitoClient(id)) {
			player.world.playSound(player.posX, player.posY, player.posZ, Main.SPLASH_SOUND,
					SoundCategory.PLAYERS, 1.0f, 0.8f + rand.nextFloat() * 0.4f, false);
			Vec3d look = player.getLookVec();
			// vector perpendicular to look on horizontal plane
			Vec3d right = new Vec3d(-look.z, 0, look.x).normalize();
			Vec3d center = new Vec3d(player.posX + look.x * 0.6, player.posY + player.getEyeHeight() + look.y * 0.6,
					player.posZ + look.z * 0.6);
			for (int i = 0; i < 15; i++) {
				double px = center.x + right.x * (rand.nextDouble() * 0.8 - 0.4);
				double py = center.y + rand.nextDouble() * 0.8 - 0.4;
				double pz = center.z + right.z * (rand.nextDouble() * 0.8 - 0.4);
				Minecraft.getMinecraft().effectRenderer.addEffect(new MosquitoParticle(player.world, px, py, pz, 0, 0, 0));
			}
		} else {
			mosquitoes.chaseAwayMosquitoClient(id);
		}
	}

	public static void removeTick() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player != null) {
			Ticks.getTicks(player).remove();
		}
	}

	public static void spawnMosquitoes(int count) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player != null && count > 0) {
			Mosquitoes mosquitoes = Mosquitoes.getMosquitoes(player);
			mosquitoes.mosquitoArrives(count);
		}
	}

	public static void synchMosquitoes(NBTTagCompound compound) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player != null && compound != null) {
			Mosquitoes mosquitoes = Mosquitoes.getMosquitoes(player);
			mosquitoes.load(compound);
		}
	}

	public static void synchTicks(NBTTagCompound compound) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player != null && compound != null) {
			Ticks ticks = Ticks.getTicks(player);
			ticks.load(compound);
		}
	}
}
