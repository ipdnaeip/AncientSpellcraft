package com.windanesz.ancientspellcraft.entity.projectile;

import com.windanesz.ancientspellcraft.entity.living.EntitySpiritBear;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.entity.construct.EntityBlackHole;
import electroblob.wizardry.entity.construct.EntityDecay;
import electroblob.wizardry.entity.construct.EntityForcefield;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.entity.living.EntitySpiritHorse;
import electroblob.wizardry.entity.living.EntitySpiritWolf;
import electroblob.wizardry.entity.living.EntitySummonedCreature;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.util.ParticleBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

import static electroblob.wizardry.util.WizardryUtilities.getEntitiesWithinRadius;

public class EntityDispelMagic extends EntityMagicProjectile {
	protected int lifetime = 16;

	public EntityDispelMagic(World world) {
		super(world);
		this.setSize(0.7f, 0.7f);

	}

	@Override
	protected void onImpact(RayTraceResult rayTrace) {

		if (!world.isRemote) {

			Entity entity = rayTrace.entityHit;

			if (entity == null) {
				List<Entity> entitiesWithinRadius = getEntitiesWithinRadius(1, this.posX, this.posY, this.posZ, world, Entity.class);
				entitiesWithinRadius.remove(this);
				if (!entitiesWithinRadius.isEmpty()) {
					System.out.println("test here)");
					entity = entitiesWithinRadius.get(0);
				}
			}
			if (entity != null) {

				// magic projectile collision, it should delete the projectile entity
				if (entity instanceof EntityMagicProjectile) {
					System.out.println("hitting a projectile");
					((EntityMagicProjectile) entity).setDead();
					System.out.println("Setting it dead");

					// magic construct collision, it should dispel or rapidly accelerate the timer of the construct,
					// maybe this should be moved to a status effect
				} else if (entity instanceof EntityMagicConstruct) {
					System.out.println("hitting a construct");
					EntityMagicConstruct construct = ((EntityMagicConstruct) entity);
					if (construct.lifetime == -1) {
						construct.setDead();
					}
					System.out.println("original lifetime: " + construct.lifetime);
					construct.lifetime = (int) (construct.lifetime * 0.1);
					System.out.println("new lifetime: " + construct.lifetime);

					Tier tier = getConstructTier(construct);

					System.out.println("construct tier: " + tier);

					// living entity, the spell should purge the PotionMagicEffect (except curses and containment) effects
				} else if (entity instanceof EntityLivingBase) {
					System.out.println("hittin a living entity");
					// magic entity or summon, possibly should be dispelled
					if ((isMagicEntityOrSummon(entity))) {
						System.out.println("entity is a summon");
						entity.setDead();
						if (this.world.isRemote) {
							for (int i = 0; i < 15; i++) {
								this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (double) this.rand.nextFloat(),
										this.posY + 1 + (double) this.rand.nextFloat(),
										this.posZ + (double) this.rand.nextFloat(), 0, 0, 0);
							}
						}
					} else {

						Collection<PotionEffect> effects = ((EntityLivingBase) entity).getActivePotionEffects();

//						for (PotionEffect effect : effects) {
//							// The PotionEffect version (as opposed to Potion) does not call cleanup callbacks
//							if (effect.getPotion() instanceof PotionMagicEffect && !(effect.getPotion() instanceof Curse || effect.getPotion() instanceof PotionContainment)) {
//								System.out.println("duration: " + effect.getDuration());
//								//								if (effect.getDuration() < 36000) { // half an hour, consider it as permanent
//								System.out.println("effect should be removed");
//								if (((EntityLivingBase) entity).isPotionActive(effect.getPotion())) {
//									((EntityLivingBase) entity).removePotionEffect(effect.getPotion());
//								}
//							}
//						}
						this.setDead();
					}

				}
			} else {
				this.setDead();
			}

			//this.playSound(WizardrySounds.ENTITY_MAGIC_FIREBALL_HIT, 2, 0.8f + rand.nextFloat() * 0.3f);

			this.setDead();
		}
	}

	private Tier getConstructTier(EntityMagicConstruct construct) {
		if (construct instanceof EntityForcefield || construct instanceof EntityDecay) {
			return Tier.ADVANCED;
		} else if (construct instanceof EntityBlackHole) {
			return Tier.MASTER;
		} else {
			return Tier.APPRENTICE;
		}
	}

	public static boolean isMagicEntityOrSummon(Entity entity) {
		return entity instanceof ISummonedCreature || entity instanceof EntitySummonedCreature || entity instanceof EntitySpiritWolf || entity instanceof EntitySpiritBear
				|| entity instanceof EntitySpiritHorse || (entity instanceof EntityIronGolem && ((EntityIronGolem) entity).isPlayerCreated());
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (!world.isRemote) {
			List<EntityForcefield> test = getEntitiesWithinRadius(5, this.posX, this.posY, this.posZ, world, EntityForcefield.class);
			if (!test.isEmpty()) {
				EntityForcefield forcefield = test.get(0);
				float radius = forcefield.getRadius();
				double search_border_size = 4;
				List<EntityDispelMagic> list = getEntitiesWithinRadius(radius + search_border_size, forcefield.posX, forcefield.posY, forcefield.posZ, world, EntityDispelMagic.class);
				if (!list.isEmpty()) {
					System.out.println("list not empty, reducing forcefield lifetime");
					forcefield.lifetime = (int) (forcefield.lifetime * 0.2);
					this.setDead();
				}
			}
		}
		//		TODO

		if (world.isRemote) {

			for (int i = 0; i < 5; i++) {

				double dx = (rand.nextDouble() - 0.5) * width;
				double dy = (rand.nextDouble() - 0.5) * height + this.height / 2 - 0.1; // -0.1 because flames aren't centred
				double dz = (rand.nextDouble() - 0.5) * width;
				double v = 0.06;
				ParticleBuilder.create(ParticleBuilder.Type.SCORCH).clr(255, 255, 255)
						.pos(this.getPositionVector().add(dx - this.motionX / 2, dy, dz - this.motionZ / 2))
						.vel(-v * dx, -v * dy, -v * dz).scale(width * 2).time(10).spawn(world);

				if (ticksExisted > 1) {

					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX, posY, posZ, 0.03, true).clr(255, 255, 255).fade(0, 0, 0)
							.time(20 + rand.nextInt(10)).spawn(world);

					if (this.ticksExisted > 1) { // Don't spawn particles behind where it started!
						double x = posX - motionX / 2;
						double y = posY - motionY / 2;
						double z = posZ - motionZ / 2;
						ParticleBuilder.create(ParticleBuilder.Type.FLASH, rand, x, y, z, 0.03, true).clr(255, 255, 255).fade(0, 0, 0)
								.time(20 + rand.nextInt(10)).spawn(world);
					}

					//									dx = (rand.nextDouble() - 0.5) * width;
					//									dy = (rand.nextDouble() - 0.5) * height + this.height / 2 - 0.1;
					//									dz = (rand.nextDouble() - 0.5) * width;
					//									ParticleBuilder.create(ParticleBuilder.Type.SCORCH)
					//											.pos(this.getPositionVector().add(dx - this.motionX, dy, dz - this.motionZ))
					//											.vel(-v * dx, -v * dy, -v * dz).scale(width * 2).time(10).spawn(world);
				}
			}
		}

	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 1.0F;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {

		if (this.isEntityInvulnerable(source)) {
			return false;

		} else {

			this.markVelocityChanged();

			if (source.getTrueSource() != null) {

				Vec3d vec3d = source.getTrueSource().getLookVec();

				if (vec3d != null) {

					double speed = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);

					this.motionX = vec3d.x * speed;
					this.motionY = vec3d.y * speed;
					this.motionZ = vec3d.z * speed;

					this.lifetime = 160;

				}

				if (source.getTrueSource() instanceof EntityLivingBase) {
					this.setCaster((EntityLivingBase) source.getTrueSource());
				}

				return true;

			} else {
				return false;
			}
		}
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	public int getLifetime() {
		return lifetime;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(lifetime);
		super.writeSpawnData(buffer);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		lifetime = buffer.readInt();
		super.readSpawnData(buffer);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		lifetime = nbttagcompound.getInteger("lifetime");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("lifetime", lifetime);
	}

}