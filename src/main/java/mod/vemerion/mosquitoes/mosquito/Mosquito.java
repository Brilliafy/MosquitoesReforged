package mod.vemerion.mosquitoes.mosquito;

import java.util.Random;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

public class Mosquito {
	// Super small scale (2x smaller)
	public static final float SCALE = 0.0075f;
	// Pixel-perfect hit threshold (squared distance in camera coordinate space)
	public static final float HIT_RADIUS_SQ = 0.0008f;

	private float x, y, prevX, prevY, targetX, targetY, rotation, legRotation, prevLegRotation;
	private float dartX, dartY;
	private float prevPlayerYaw, prevPlayerPitch;
	private boolean hasPrevPlayerAngles;
	private int ticksExisted;
	private Random rand;
	private int id;

	public Mosquito(Random rand, int id, int ticksExisted) {
		this.rand = rand;
		this.ticksExisted = ticksExisted;
		this.id = id;
		rotation = rand.nextFloat() * 360;
		initPos();
		prevX = x;
		prevY = y;
	}

	private void initPos() {
		targetX = randomXPos();
		targetY = randomYPos();
		if (ticksExisted < arrivingDuration() * 0.4f) {
			x = randomXPosOutside();
			y = randomYPosOutside();
		} else {
			x = targetX;
			y = targetY;
		}

		if (ticksExisted > startsLeavingAfter()) {
			targetX = randomXPosOutside();
			targetY = randomYPosOutside();
		}
	}

	public Mosquito(Random rand, int id) {
		this(rand, id, 0);
	}

	public void tick(EntityPlayer player) {
		ticksExisted++;

		if (player.world.isRemote) {
			prevX = x;
			prevY = y;
			prevLegRotation = legRotation;

			// Respond to player camera turning so crosshair aiming works naturally
			if (hasPrevPlayerAngles) {
				float yawDiff = player.rotationYaw - prevPlayerYaw;
				float pitchDiff = player.rotationPitch - prevPlayerPitch;
				// Wrap yawDiff to [-180, 180]
				yawDiff = MathHelper.wrapDegrees(yawDiff);
				x -= yawDiff * 0.009f;
				y -= pitchDiff * 0.009f;
			} else {
				hasPrevPlayerAngles = true;
			}
			prevPlayerYaw = player.rotationYaw;
			prevPlayerPitch = player.rotationPitch;

			if (isArriving()) {
				// Frequent erratic target changes
				if (rand.nextDouble() < 0.15) {
					targetX = randomXPos();
					targetY = randomYPos();
				}

				// Sudden erratic darting impulses
				if (rand.nextInt(4) == 0) {
					dartX = (rand.nextFloat() - 0.5f) * 0.05f;
					dartY = (rand.nextFloat() - 0.5f) * 0.05f;
				}
				dartX *= 0.75f;
				dartY *= 0.75f;

				// High-speed pursuit
				float dx = targetX - x;
				float dy = targetY - y;
				float dist = (float) Math.sqrt(dx * dx + dy * dy);
				float speed = dist < 0.001f ? 0.01f : dist < 0.15f ? 0.04f + 0.03f * (dist / 0.15f) : 0.075f;
				if (dist > 0.0001f) {
					x += (dx / dist) * speed;
					y += (dy / dist) * speed;
				}

				// Noisy, high-frequency chaotic pattern
				float noiseX = (float) (Math.sin(ticksExisted * 0.85f + id * 13.0f) * 0.016f
						+ Math.cos(ticksExisted * 2.1f + id * 29.0f) * 0.020f);
				float noiseY = (float) (Math.cos(ticksExisted * 0.95f + id * 19.0f) * 0.016f
						+ Math.sin(ticksExisted * 2.3f + id * 37.0f) * 0.020f);

				x += noiseX + dartX;
				y += noiseY + dartY;

				// Clamp to general viewing frustum when flying in front of face
				x = MathHelper.clamp(x, -0.32f, 0.32f);
				y = MathHelper.clamp(y, -0.22f, 0.22f);

			} else if (isLeaving()) {
				float dx = targetX - x;
				float dy = targetY - y;
				float dist = (float) Math.sqrt(dx * dx + dy * dy);
				float speed = 0.08f;
				if (dist > 0.0001f) {
					x += (dx / dist) * speed;
					y += (dy / dist) * speed;
				}

				if (ticksExisted() - 1 == startsLeavingAfter()) {
					targetX = randomXPosOutside();
					targetY = randomYPosOutside();
				}
			}

			if (prevX < x) {
				legRotation = (float) MathHelper.clampedLerp(legRotation, 20, 0.15);
			} else {
				legRotation = (float) MathHelper.clampedLerp(legRotation, -20, 0.15);
			}

		} else {
			if (isSucking() && hasMalaria()) {
				int armorCount = 0;
				for (ItemStack armor : player.inventory.armorInventory) {
					if (!armor.isEmpty()) {
						armorCount++;
					}
				}
				// 0.8x less likely per armor piece worn, max 3.2x with full armor set
				double biteChance = 1.0D / (1.0D + armorCount * 0.55D);
				if (rand.nextDouble() < (0.003D * biteChance) && !player.isPotionActive(Main.MALARIA_EFFECT)) {
					player.addPotionEffect(new PotionEffect(Main.MALARIA_EFFECT, 20 * 60 * 50));
				}
			}
		}
	}

	public boolean isHit(float hitX, float hitY) {
		float dx = x - hitX;
		float dy = y - hitY;
		return (dx * dx + dy * dy) < HIT_RADIUS_SQ;
	}

	private boolean hasMalaria() {
		return id % 10 == 0;
	}

	private float randomYPos() {
		return rand.nextFloat() * 0.28f - 0.14f;
	}

	private float randomXPos() {
		if (rand.nextBoolean()) {
			return rand.nextFloat() * 0.05f - 0.025f - 0.15f;
		} else {
			return rand.nextFloat() * 0.05f - 0.025f + 0.15f;
		}
	}

	private float randomXPosOutside() {
		return MathHelper.cos((float) Math.toRadians(rotation)) * 1.5f;
	}

	private float randomYPosOutside() {
		return MathHelper.sin((float) Math.toRadians(rotation)) * 1.5f;
	}

	public int ticksExisted() {
		return ticksExisted;
	}

	public int getId() {
		return id;
	}

	public void chaseAway() {
		if (ticksExisted < startsLeavingAfter())
			ticksExisted = startsLeavingAfter();
	}

	public float getX(float partialTicks) {
		return prevX + (x - prevX) * partialTicks;
	}

	public float getY(float partialTicks) {
		return prevY + (y - prevY) * partialTicks;
	}

	public float getLegRotation(float partialTicks) {
		return prevLegRotation + (legRotation - prevLegRotation) * partialTicks;
	}

	public float getZ(float partialTicks) {
		float ageInTicks = ticksExisted() + partialTicks;
		double z = isArriving() ? -0.2 - MathHelper.clampedLerp(0.2, 0, ageInTicks / arrivingDuration())
				: -0.2;
		return (float) z;
	}

	public float getScale(float partialTicks) {
		return SCALE;
	}

	public float getRotationX() {
		return isArriving() ? -180 : isLeaving() ? -180 : 90;
	}

	public float getRotationY() {
		return isArriving() ? 0 : isLeaving() ? 0 : rotation;
	}

	public int startsSuckingAfter() {
		return 160;
	}

	public int startsLeavingAfter() {
		return 260;
	}

	public int arrivingDuration() {
		return 120;
	}

	public int leavingDuration() {
		return 30;
	}

	public boolean hasArrived() {
		return ticksExisted() >= arrivingDuration();
	}

	public boolean isArriving() {
		return ticksExisted() < arrivingDuration();
	}

	public boolean isLeaving() {
		return ticksExisted() > startsLeavingAfter();
	}

	public boolean hasLeft() {
		return ticksExisted() >= startsLeavingAfter() + leavingDuration();
	}

	public boolean isSucking() {
		return ticksExisted() > startsSuckingAfter() && ticksExisted() < startsLeavingAfter();
	}

	public boolean isFlying() {
		return isArriving() || isLeaving();
	}
}
