package mod.vemerion.mosquitoes.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MosquitoParticle extends Particle {

	public MosquitoParticle(World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(world, x, y, z, xSpeed, ySpeed, zSpeed);
		this.particleMaxAge = 15 + this.rand.nextInt(10);
		this.particleGravity = 0.8F;
		this.particleScale = 0.6F + this.rand.nextFloat() * 0.4F;
		this.particleRed = 0.25F + this.rand.nextFloat() * 0.2F;
		this.particleGreen = 0.02F;
		this.particleBlue = 0.02F;
		this.motionX = (this.rand.nextDouble() * 2.0 - 1.0) * 0.06;
		this.motionY = this.rand.nextDouble() * 0.08 + 0.02;
		this.motionZ = (this.rand.nextDouble() * 2.0 - 1.0) * 0.06;
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		return 15728880;
	}

	@Override
	public int getFXLayer() {
		return 0;
	}
}
