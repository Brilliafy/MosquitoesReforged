package mod.vemerion.mosquitoes.entity;

import java.util.List;

import mod.vemerion.mosquitoes.Main;
import mod.vemerion.mosquitoes.mosquito.Mosquitoes;
import mod.vemerion.mosquitoes.tick.Ticks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class EntityTick extends EntityCreature implements IMob {

	public EntityTick(World worldIn) {
		super(worldIn);
		// Incredibly small hitbox - pixel perfect
		this.setSize(0.1F, 0.05F);
		this.experienceValue = 1;
		this.setHealth(2.0F);
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		// Prevent tiny entity from being culled past 6-10 blocks
		double d0 = 64.0D * getRenderDistanceWeight();
		return distance < d0 * d0;
	}

	public static boolean isTargetable(EntityLivingBase entity) {
		if (entity == null || !entity.isEntityAlive())
			return false;
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			return !player.isCreative() && !player.isSpectator();
		}
		if (entity instanceof IMob || entity instanceof EntityTick || entity instanceof EntityMosquito) {
			return false;
		}
		return entity instanceof EntityAnimal
				|| entity instanceof net.minecraft.entity.passive.EntityVillager
				|| entity instanceof net.minecraft.entity.INpc
				|| entity instanceof net.minecraft.entity.passive.IAnimals
				|| entity instanceof net.minecraft.entity.passive.EntityAmbientCreature;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAITickLeap(this));
		this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.4D, false));
		// Waits/idles in vegetation, rarely crawls around
		this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 0.4D, 180));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		// Targets closest player, villager, or passive animal
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false,
				EntityTick::isTargetable));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.16D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		// Prevent 0.05-height entity from suffocating in the grass block under its feet
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		// Immune to suffocation inside blocks and fall damage
		if (source == DamageSource.IN_WALL || source == DamageSource.FALL || source == DamageSource.DROWN) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	public void updateRidden() {
		Entity host = this.getRidingEntity();
		if (this.isRiding() && host != null && !host.isDead) {
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
			this.onUpdate();

			if (!this.isRiding())
				return;

			positionOnHost(host);
		} else {
			super.updateRidden();
		}
	}

	private void positionOnHost(Entity host) {
		float yaw = host instanceof EntityLivingBase ? ((EntityLivingBase) host).renderYawOffset : host.rotationYaw;
		float radYaw = yaw * 0.017453292F;
		double sin = MathHelper.sin(radYaw);
		double cos = MathHelper.cos(radYaw);

		boolean isBiped = host.height > host.width * 1.4F;

		int id = this.getEntityId();
		int slot = Math.abs(id) % 4;

		double targetX, targetY, targetZ;

		// Direction vectors:
		// Forward look vector: x = -sin, z = cos
		// Backward vector: x = sin, z = -cos
		// Right vector: x = -cos, z = -sin
		double backX = sin;
		double backZ = -cos;
		double rightX = -cos;
		double rightZ = -sin;

		if (isBiped) {
			// Clinging to the outside surface of the back or shoulders of the villager
			double attachY = host.height * 0.72D;
			double backDist = (host.width / 2.0D) + 0.10D; // Firmly outside the villager's back
			double sideDist = 0.0D;

			if (slot == 0) {
				attachY = host.height * 0.74D;
				sideDist = 0.0D;
			} else if (slot == 1) {
				attachY = host.height * 0.78D;
				sideDist = -0.16D;
			} else if (slot == 2) {
				attachY = host.height * 0.78D;
				sideDist = 0.16D;
			} else {
				attachY = host.height * 0.62D;
				sideDist = 0.05D;
			}

			targetX = host.posX + (backX * backDist) + (rightX * sideDist);
			targetY = host.posY + attachY;
			targetZ = host.posZ + (backZ * backDist) + (rightZ * sideDist);

			this.rotationYaw = yaw;
			this.renderYawOffset = yaw;
		} else {
			// Quadruped (Cow, Sheep, Pig, Horse): sitting on TOP of the back
			double topY = host.height + 0.02D;
			double lengthDist = 0.0D;
			double widthDist = 0.0D;

			if (slot == 0) {
				lengthDist = 0.0D;
				widthDist = 0.0D;
			} else if (slot == 1) {
				lengthDist = -host.width * 0.35D;
				widthDist = -host.width * 0.20D;
			} else if (slot == 2) {
				lengthDist = host.width * 0.35D;
				widthDist = host.width * 0.20D;
			} else {
				lengthDist = 0.0D;
				widthDist = host.width * 0.30D;
			}

			targetX = host.posX + (backX * lengthDist) + (rightX * widthDist);
			targetY = host.posY + topY;
			targetZ = host.posZ + (backZ * lengthDist) + (rightZ * widthDist);

			this.rotationYaw = yaw;
			this.renderYawOffset = yaw;
		}

		this.setPosition(targetX, targetY, targetZ);
		this.prevPosX = targetX;
		this.prevPosY = targetY;
		this.prevPosZ = targetZ;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!this.world.isRemote) {
			if (this.isRiding()) {
				Entity host = this.getRidingEntity();
				if (host == null || host.isDead) {
					this.dismountRidingEntity();
				} else {
					// Sucking blood from host mob every 3 seconds
					if (this.ticksExisted % 60 == 0) {
						host.attackEntityFrom(DamageSource.causeMobDamage(this), 0.5F);
						if (host instanceof EntityLivingBase) {
							((EntityLivingBase) host).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 0));
						}
						this.playSound(Main.GULP_SOUND, 0.8F, 1.0F);
					}
				}
			} else {
				EntityLivingBase target = this.getAttackTarget();
				if (target == null || target.isDead) {
					List<EntityLivingBase> candidates = this.world.getEntitiesWithinAABB(
							EntityLivingBase.class, this.getEntityBoundingBox().grow(6.0D, 2.5D, 6.0D),
							EntityTick::isTargetable);
					double closestDist = Double.MAX_VALUE;
					for (EntityLivingBase candidate : candidates) {
						double d = this.getDistanceSq(candidate);
						if (d < closestDist) {
							closestDist = d;
							target = candidate;
						}
					}
					if (target != null) {
						this.setAttackTarget(target);
					}
				}

				if (target != null && target.isEntityAlive()) {
					if (target instanceof EntityPlayer) {
						EntityPlayer playerTarget = (EntityPlayer) target;
						if (!playerTarget.isCreative() && !playerTarget.isSpectator()) {
							if (this.getDistanceSq(playerTarget) < 1.69D || this.getEntityBoundingBox().intersects(playerTarget.getEntityBoundingBox())) {
								Ticks ticks = Ticks.getTicks(playerTarget);
								if (!ticks.hasTick()) {
									// Armor check: 1.5x less likely per armor piece worn (~5x with full armor)
									int armorCount = 0;
									for (ItemStack armor : playerTarget.inventory.armorInventory) {
										if (!armor.isEmpty()) {
											armorCount++;
										}
									}
									double attachChance = 1.0D / Math.pow(1.5D, armorCount);
									if (this.rand.nextDouble() < attachChance) {
										// Successfully attached onto player!
										ticks.createTick(playerTarget);
										playerTarget.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 20 * 20));
										this.playSound(Main.TEAR_SOUND, 0.8F, 1.0F);
										this.setDead();
									} else {
										// Deflected by armor! Clink sound and bounce off
										playerTarget.world.playSound(null, playerTarget.posX, playerTarget.posY, playerTarget.posZ,
												SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 0.8F,
												1.5F + this.rand.nextFloat() * 0.4F);
										this.motionX = (this.rand.nextDouble() - 0.5D) * 0.3D;
										this.motionY = 0.25D;
										this.motionZ = (this.rand.nextDouble() - 0.5D) * 0.3D;
									}
								} else {
									// Already has a tick attached: bite player
									this.attackEntityAsMob(playerTarget);
								}
							}
						}
					} else if (isTargetable(target)) {
						// Attacking mob (passive animal, villager, NPC)
						if (this.getDistanceSq(target) < 2.25D || this.getEntityBoundingBox().intersects(target.getEntityBoundingBox())) {
							int tickCount = 0;
							for (Entity passenger : target.getPassengers()) {
								if (passenger instanceof EntityTick) {
									tickCount++;
								}
							}
							if (tickCount < 4) {
								// Attach onto the mob!
								this.startRiding(target, true);
								target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 20, 0));
								this.playSound(Main.TEAR_SOUND, 0.8F, 1.0F);
							} else {
								this.attackEntityAsMob(target);
								this.playSound(Main.GULP_SOUND, 0.8F, 1.0F);
							}
						}
					}
				}
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
		// Surface check: must be at surface level (Y >= 50 or skylight), NOT deep in underground caves
		if (this.posY < 50 && !this.world.canSeeSky(this.getPosition())) {
			return false;
		}

		// Never spawn in cold biomes or disallowed dimensions/biomes
		Biome biome = this.world.getBiome(this.getPosition());
		if (!Mosquitoes.isBiomeAllowed(biome) || Mosquitoes.isColdBiome(biome)) {
			return false;
		}

		// Must have vegetation nearby (grass, flowers, crops, trees/leaves, bushes with mod support)
		if (!mod.vemerion.mosquitoes.util.VegetationHelper.hasLotsOfVegetation(this.world, this.getPosition())) {
			return false;
		}

		// Strict density cap: maximum 2 ticks in a 32-block radius (prevents swarming while guaranteeing ticks appear)
		List<EntityTick> nearbyTicks = this.world.getEntitiesWithinAABB(
				EntityTick.class, this.getEntityBoundingBox().grow(32.0D, 16.0D, 32.0D));
		if (nearbyTicks.size() >= 2) {
			return false;
		}

		// Spawns near/in vegetation regardless of time (day and night)!
		return this.world.checkNoEntityCollision(this.getEntityBoundingBox())
				&& this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()
				&& !this.world.containsAnyLiquid(this.getEntityBoundingBox());
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
	protected void playStepSound(BlockPos pos, Block blockIn) {
		// Silent - ticks make no footstep or walking sounds
	}
}
