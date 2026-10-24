package mod.vemerion.mosquitoes.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class EntityAITickLeap extends EntityAIBase {
	private final EntityTick tick;
	private EntityLivingBase leapTarget;
	private int cooldown;

	public EntityAITickLeap(EntityTick tick) {
		this.tick = tick;
		this.setMutexBits(5);
	}

	@Override
	public boolean shouldExecute() {
		if (this.tick.isRiding()) {
			return false;
		}

		if (cooldown > 0) {
			cooldown--;
			return false;
		}

		this.leapTarget = this.tick.getAttackTarget();
		if (this.leapTarget == null) {
			return false;
		}

		if (this.leapTarget instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) this.leapTarget;
			if (player.isCreative() || player.isSpectator()) {
				return false;
			}
		}

		double distSq = this.tick.getDistanceSq(this.leapTarget);
		// Can leap when between 0.4 and 3.5 blocks away (distSq up to ~12.5)
		if (distSq >= 0.4D && distSq <= 12.5D) {
			if (!this.tick.onGround) {
				return false;
			}
			return this.tick.getRNG().nextInt(2) == 0;
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.tick.onGround;
	}

	@Override
	public void startExecuting() {
		cooldown = 15;

		double dx = this.leapTarget.posX - this.tick.posX;
		double dz = this.leapTarget.posZ - this.tick.posZ;
		double dy = this.leapTarget.posY - this.tick.posY;
		float dist = MathHelper.sqrt(dx * dx + dz * dz);

		if (dist >= 1.0E-4F) {
			// Leap up to 3 blocks far horizontally
			double speed = 0.48D;
			this.tick.motionX = (dx / dist) * speed;
			this.tick.motionZ = (dz / dist) * speed;
		}

		// Leap up to 2 blocks high vertically (0.58F reaches ~2 blocks height)
		float jumpY = 0.58F;
		if (dy > 0) {
			jumpY += (float) Math.min(dy * 0.15D, 0.15D);
		}
		this.tick.motionY = jumpY;
	}
}
