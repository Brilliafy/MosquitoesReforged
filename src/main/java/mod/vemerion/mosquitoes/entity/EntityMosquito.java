package mod.vemerion.mosquitoes.entity;

import java.util.List;

import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class EntityMosquito extends EntityFlying implements IMob {

	private static final DamageSource SUCKING = new DamageSource("sucking").setDamageBypassesArmor();
	private BlockPos targetPos;
	private int biteCooldown = 0;
	private float dartX, dartY, dartZ;
	private int currentBuzzIndex = -1;
	private int buzzTimer = 0;

	public EntityMosquito(World worldIn) {
		super(worldIn);
		// Incredibly small hitbox - pixel perfect (2x smaller)
		this.setSize(0.05F, 0.05F);
		this.experienceValue = 1;
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		// Prevent Minecraft from culling tiny 0.05-sized entities past 6-10 blocks!
		double d0 = 64.0D * getRenderDistanceWeight();
		return distance < d0 * d0;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (biteCooldown > 0) {
			biteCooldown--;
		}

		if (this.world.isRemote && this.isEntityAlive()) {
			// Continuous buzzing with seamless crossfade and clip alternation
			if (buzzTimer <= 0) {
				currentBuzzIndex = Main.getNextRandomBuzzIndex(this.rand, currentBuzzIndex);
				SoundEvent sound = Main.FLYING_MOSQUITO_SOUNDS[currentBuzzIndex];
				if (sound != null) {
					float pitch = 0.96F + this.rand.nextFloat() * 0.08F;
					this.world.playSound(this.posX, this.posY, this.posZ, sound, SoundCategory.HOSTILE, 0.65F, pitch, false);
				}
				buzzTimer = 20; // 2-tick (~100ms) crossfade overlap before 22-tick clip ends
			}
			buzzTimer--;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null; // Return null so vanilla living sound interval does not interrupt buzzing
	}

	@Override
	protected float getSoundVolume() {
		return 0.7F;
	}

	@Override
	public int getTalkInterval() {
		return 15;
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source == DamageSource.IN_WALL || source == DamageSource.FALL || source == DamageSource.DROWN) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		updateFlight();
	}

	public static boolean isTargetable(EntityLivingBase entity) {
		if (entity == null || !entity.isEntityAlive())
			return false;
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			return !player.isCreative() && !player.isSpectator() && !player.isPotionActive(Main.CITRONELLA_EFFECT);
		}
		if (entity instanceof IMob || entity instanceof EntityMosquito || entity instanceof EntityTick) {
			return false;
		}
		return entity instanceof EntityAnimal
				|| entity instanceof net.minecraft.entity.passive.EntityVillager
				|| entity instanceof net.minecraft.entity.INpc
				|| entity instanceof net.minecraft.entity.passive.IAnimals
				|| entity instanceof net.minecraft.entity.passive.EntityAmbientCreature;
	}

	private void updateFlight() {
		// Repelled by Citronella effect
		EntityPlayer nearestPlayer = this.world.getClosestPlayerToEntity(this, 24.0D);
		if (nearestPlayer != null && nearestPlayer.isPotionActive(Main.CITRONELLA_EFFECT)) {
			// Fly away from player
			Vec3d flee = new Vec3d(this.posX - nearestPlayer.posX, this.posY - nearestPlayer.posY, this.posZ - nearestPlayer.posZ).normalize();
			this.motionX += flee.x * 0.1D;
			this.motionY += flee.y * 0.05D;
			this.motionZ += flee.z * 0.1D;
			return;
		}

		// Target: closest passive entity (animals, villagers, NPCs) or player
		EntityLivingBase target = null;
		List<EntityLivingBase> candidates = this.world.getEntitiesWithinAABB(
				EntityLivingBase.class, this.getEntityBoundingBox().grow(16.0D, 8.0D, 16.0D),
				EntityMosquito::isTargetable);
		double closestDist = Double.MAX_VALUE;
		for (EntityLivingBase candidate : candidates) {
			double d = this.getDistanceSq(candidate);
			if (d < closestDist) {
				closestDist = d;
				target = candidate;
			}
		}

		if (target != null && target.isEntityAlive()) {
			targetPos = new BlockPos(target.posX, target.posY + target.getEyeHeight() * 0.8D, target.posZ);

			if (target instanceof EntityPlayer) {
				EntityPlayer playerTarget = (EntityPlayer) target;
				// Holding right click with the swatter swats mosquito if it comes near!
				if (playerTarget.isHandActive() && playerTarget.getActiveItemStack().getItem() == Main.SWATTER_ITEM) {
					if (this.getDistanceSq(playerTarget) <= 9.0D) { // within 3 blocks
						this.attackEntityFrom(DamageSource.causePlayerDamage(playerTarget), 10.0F);
						playerTarget.getActiveItemStack().damageItem(1, playerTarget);
						playerTarget.swingArm(playerTarget.getActiveHand());
						return;
					}
				}

				// Bite player if in contact
				if (this.getDistanceSq(playerTarget) < 1.4D && biteCooldown <= 0) {
					biteCooldown = 30;
					int armorCount = 0;
					for (ItemStack armor : playerTarget.inventory.armorInventory) {
						if (!armor.isEmpty()) {
							armorCount++;
						}
					}
					// 0.8x less likely per armor piece worn, max 3.2x with full armor set
					double biteChance = 1.0D / (1.0D + armorCount * 0.55D);
					if (this.rand.nextDouble() < biteChance) {
						playerTarget.attackEntityFrom(SUCKING, 1.0F);
						this.playSound(Main.GULP_SOUND, 0.8F, 0.8F + this.rand.nextFloat() * 0.4F);
						if (this.rand.nextDouble() < 0.10 && !playerTarget.isPotionActive(Main.MALARIA_EFFECT)) {
							playerTarget.addPotionEffect(new PotionEffect(Main.MALARIA_EFFECT, 20 * 60 * 50));
						}
					} else {
						// Deflected by armor! Clink sound and bounce off
						playerTarget.world.playSound(null, playerTarget.posX, playerTarget.posY, playerTarget.posZ,
								SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 0.6F,
								1.6F + this.rand.nextFloat() * 0.4F);
						this.motionX = (this.rand.nextDouble() - 0.5D) * 0.25D;
						this.motionY = 0.2D;
						this.motionZ = (this.rand.nextDouble() - 0.5D) * 0.25D;
					}
				}
			} else {
				// Bite passive entity (animal, villager, NPC) if in contact
				if (this.getDistanceSq(target) < 1.4D && biteCooldown <= 0) {
					biteCooldown = 30;
					target.attackEntityFrom(SUCKING, 1.0F);
					this.playSound(Main.GULP_SOUND, 0.8F, 0.8F + this.rand.nextFloat() * 0.4F);
					if (this.rand.nextDouble() < 0.25D) {
						target.addPotionEffect(new PotionEffect(MobEffects.POISON, 20 * 5));
					}
					target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 8, 0));
				}
			}
		} else {
			if (targetPos == null || this.rand.nextInt(25) == 0 || this.getDistanceSqToCenter(targetPos) < 2.0D) {
				targetPos = new BlockPos(this.posX + (this.rand.nextDouble() - 0.5D) * 16.0D,
						this.posY + (this.rand.nextDouble() - 0.5D) * 6.0D,
						this.posZ + (this.rand.nextDouble() - 0.5D) * 16.0D);
			}
		}

		// High-speed pursuit
		double dx = (targetPos.getX() + 0.5D) - this.posX;
		double dy = (targetPos.getY() + 0.5D) - this.posY;
		double dz = (targetPos.getZ() + 0.5D) - this.posZ;
		double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if (dist > 0.1D) {
			double speed = 0.08D;
			this.motionX += (dx / dist) * speed;
			this.motionY += (dy / dist) * speed * 0.6D;
			this.motionZ += (dz / dist) * speed;
		}

		// Erratic sudden dart impulses
		if (this.rand.nextInt(4) == 0) {
			dartX = (this.rand.nextFloat() - 0.5F) * 0.12F;
			dartY = (this.rand.nextFloat() - 0.5F) * 0.08F;
			dartZ = (this.rand.nextFloat() - 0.5F) * 0.12F;
		}
		dartX *= 0.75F;
		dartY *= 0.75F;
		dartZ *= 0.75F;

		// High-frequency noisy chaotic pattern
		double noiseX = Math.sin(this.ticksExisted * 0.85 + this.getEntityId() * 13.0) * 0.035;
		double noiseY = Math.cos(this.ticksExisted * 1.15 + this.getEntityId() * 19.0) * 0.025;
		double noiseZ = Math.cos(this.ticksExisted * 0.95 + this.getEntityId() * 23.0) * 0.035;

		this.motionX += noiseX + dartX;
		this.motionY += noiseY + dartY;
		this.motionZ += noiseZ + dartZ;

		// Cap max speed
		double currentSpeedSq = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
		if (currentSpeedSq > 0.25D) {
			double scale = Math.sqrt(0.25D / currentSpeedSq);
			this.motionX *= scale;
			this.motionY *= scale;
			this.motionZ *= scale;
		}

		this.rotationYaw = -((float) MathHelper.atan2(this.motionX, this.motionZ)) * (180.0F / (float) Math.PI);
		this.renderYawOffset = this.rotationYaw;
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		if (!this.world.isRemote) {
			if (this.rand.nextDouble() < 0.15D) {
				EntityItem wing = new EntityItem(this.world, this.posX, this.posY, this.posZ,
						new ItemStack(Main.MOSQUITO_WING_ITEM));
				this.world.spawnEntity(wing);
			}
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		// Overworld dimension 0 only - never spawn in Nether, End, or any modded dimensions
		if (this.world.provider.getDimension() != 0
				|| this.world.provider.getDimensionType() != net.minecraft.world.DimensionType.OVERWORLD) {
			return false;
		}
		if (this.world.getDifficulty() == net.minecraft.world.EnumDifficulty.PEACEFUL) {
			return false;
		}
		// Surface check: must be at surface level (Y >= 50 or skylight), NOT deep in underground caves
		if (this.posY < 50 && !this.world.canSeeSky(this.getPosition())) {
			return false;
		}
		Biome biome = this.world.getBiome(this.getPosition());
		if (!Mosquitoes.isBiomeAllowed(biome) || Mosquitoes.isColdBiome(biome)) {
			return false;
		}
		boolean isSwamp = Mosquitoes.isSwamp(biome);
		boolean isNight = Mosquitoes.isNightTime(this.world);
		boolean nearWater = Mosquitoes.isNearWater(this.world, this.getPosition(), 14);

		if (isNight) {
			// At night: 100% allowed in swamps and near water!
			if (!isSwamp && !nearWater) {
				// Dry normal land: 50% chance
				if (this.rand.nextDouble() > 0.50D)
					return false;
			}
		} else {
			// Daytime: rare in swamps, rare near water, absent on dry land
			if (isSwamp) {
				if (this.rand.nextDouble() > 0.35D)
					return false;
			} else if (nearWater) {
				if (this.rand.nextDouble() > 0.15D)
					return false;
			} else {
				return false;
			}
		}

		return this.world.checkNoEntityCollision(this.getEntityBoundingBox())
				&& this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return Main.SMACK_SOUND;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return Main.SMACK_SOUND;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void playStepSound(BlockPos pos, net.minecraft.block.Block blockIn) {
		// Silent - mosquitoes make no footstep or walking sounds
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}
}
