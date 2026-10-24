package mod.vemerion.mosquitoes.tick;

import java.util.Random;

import mod.vemerion.mosquitoes.Main;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class Tick {
	private static final float SPEED = 0.00015f;
	private static final int MAX_DISEASE_TIMER = 20 * 60 * 3;
	private static final DamageSource SUCKING = new DamageSource("sucking").setDamageBypassesArmor();

	private float x, y, targetX, targetY, direction, bellySize, prevBellySize, prevX, prevY;
	private int ticksExisted;
	private int diseaseTimer;
	private int suckCooldown;
	Random rand;

	public Tick() {
		this.rand = new Random();
		this.x = randomXPosOutside();
		this.y = randomYPosOutside();
		this.prevX = x;
		this.prevY = y;
		this.targetX = randomXPos();
		this.targetY = randomYPos();
		this.direction = (float) MathHelper.atan2(targetY - y, targetX - x);
		this.bellySize = 1;
		this.prevBellySize = bellySize;
		this.diseaseTimer = MAX_DISEASE_TIMER;
		this.suckCooldown = 80;
	}

	public void load(NBTTagCompound compound) {
		x = compound.getFloat("x");
		y = compound.getFloat("y");
		this.prevX = x;
		this.prevY = y;
		targetX = compound.getFloat("targetX");
		targetY = compound.getFloat("targetY");
		direction = compound.getFloat("direction");
		bellySize = compound.getFloat("bellySize");
		prevBellySize = bellySize;
		ticksExisted = compound.getInteger("ticksExisted");
		suckCooldown = compound.hasKey("suckCooldown") ? compound.getInteger("suckCooldown") : 80;
	}

	public void save(NBTTagCompound compound) {
		compound.setFloat("x", x);
		compound.setFloat("y", y);
		compound.setFloat("targetX", targetX);
		compound.setFloat("targetY", targetY);
		compound.setFloat("direction", direction);
		compound.setFloat("bellySize", bellySize);
		compound.setInteger("ticksExisted", ticksExisted);
		compound.setInteger("suckCooldown", suckCooldown);
	}

	public int ticksExisted() {
		return ticksExisted;
	}

	public void tick(EntityPlayer player) {
		ticksExisted++;
		prevX = x;
		prevY = y;
		prevBellySize = bellySize;

		if (isMoving()) {
			x += MathHelper.cos(direction) * SPEED;
			y += MathHelper.sin(direction) * SPEED;
		} else {
			// Actively drinking blood
			bellySize += 0.00015f;

			if (!player.world.isRemote) {
				suckCooldown--;
				if (suckCooldown <= 0) {
					suckCooldown = 160; // Drinks blood every 8 seconds
					player.attackEntityFrom(SUCKING, 1.0F);
					player.world.playSound(null, player.posX, player.posY, player.posZ, Main.GULP_SOUND,
							SoundCategory.PLAYERS, 0.8F, 0.8F + rand.nextFloat() * 0.4F);
				}

				if (diseaseTimer-- < 0) {
					diseaseTimer = MAX_DISEASE_TIMER;
					player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 20 * 40));
				}
			}
		}
	}

	public float getX(float partialTicks) {
		return prevX + (x - prevX) * partialTicks;
	}

	public float getY(float partialTicks) {
		return prevY + (y - prevY) * partialTicks;
	}

	public float getBellySize(float partialTicks) {
		return prevBellySize + (bellySize - prevBellySize) * partialTicks;
	}

	public float getDirection() {
		return (float) Math.toDegrees(direction) - 90;
	}

	private float randomYPos() {
		return rand.nextFloat() * 0.05f - 0.025f;
	}

	private float randomXPos() {
		return rand.nextFloat() * 0.1f - 0.05f;
	}

	private float randomXPosOutside() {
		return rand.nextBoolean() ? -0.15f : 0.15f;
	}

	private float randomYPosOutside() {
		return rand.nextFloat() * 0.2f - 0.1f;
	}

	public boolean isMoving() {
		return distanceToTarget() > 0.0001f;
	}

	private float distanceToTarget() {
		float xDistance = x - targetX;
		float yDistance = y - targetY;
		return xDistance * xDistance + yDistance * yDistance;
	}
}
