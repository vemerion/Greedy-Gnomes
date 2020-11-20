package mod.vemerion.greedygnomes.entity;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import mod.vemerion.greedygnomes.ModInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GreedyGnomeEntity extends PathAwareEntity {

	private static final List<Quest> POSSIBLE_QUESTS = ImmutableList.of(new Quest(Items.APPLE, 20, 30),
			new Quest(Items.SNOWBALL, 20, 30), new Quest(Items.BIRCH_LOG, 50, 64),
			new Quest(Items.DARK_OAK_LOG, 50, 64), new Quest(Items.COAL, 50, 64), new Quest(Items.IRON_PICKAXE, 1, 1),
			new Quest(ModInit.GREEDY_GNOME_BUNDLE_ITEM, 1, 1));

	private static final TrackedData<ItemStack> QUEST = DataTracker.registerData(GreedyGnomeEntity.class,
			TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<Boolean> COLLECTING = DataTracker.registerData(GreedyGnomeEntity.class,
			TrackedDataHandlerRegistry.BOOLEAN);

	private int collectingProgress;
	private int prevCollectingProgress;
	private boolean hasSnowball;

	public GreedyGnomeEntity(EntityType<GreedyGnomeEntity> entityType, World world) {
		super(entityType, world);

		equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STICK));
	}

	public GreedyGnomeEntity(World world) {
		super(ModInit.GREEDY_GNOME, world);

		equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STICK));
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1);
	}

	@Override
	protected void initGoals() {
		goalSelector.add(1, new SwimGoal(this));
		goalSelector.add(2, new CollectQuestItemGoal(this));
		goalSelector.add(3, new MeleeAttackGoal(this, 2.0D, true));
		goalSelector.add(4, new ThrowSnowballGoal(this));
		goalSelector.add(5, new WanderAroundFarGoal(this, 1));
		goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		goalSelector.add(7, new LookAroundGoal(this));
		targetSelector.add(1, new RevengeGoal(this, getClass()) {
			@Override
			public boolean shouldContinue() {
				return super.shouldContinue() && getAttacker() != null;
			}
		});

	}
	
	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putBoolean("hasSnowball", hasSnowball);
		CompoundTag quest = new CompoundTag();
		getQuest().toTag(quest);
		tag.put("quest", quest);
	}
	
	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		hasSnowball = tag.getBoolean("hasSnowball");
		setQuest(ItemStack.fromTag(tag.getCompound("quest")));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		dataTracker.startTracking(QUEST, randomQuest());
		dataTracker.startTracking(COLLECTING, false);
	}

	private void setCollecting(boolean collecting) {
		dataTracker.set(COLLECTING, collecting);
	}

	public boolean isCollecting() {
		return dataTracker.get(COLLECTING);
	}

	public ItemStack getQuest() {
		return dataTracker.get(QUEST);
	}

	private void setQuest(ItemStack questStack) {
		dataTracker.set(QUEST, questStack);
	}

	private void changeQuest() {
		setQuest(randomQuest());
	}

	private ItemStack randomQuest() {
		return POSSIBLE_QUESTS.get(getRandom().nextInt(POSSIBLE_QUESTS.size())).createQuestInstance(getRandom());
	}

	private void giveSnowball() {
		this.hasSnowball = true;
	}

	private void throwSnowball(Entity target) {
		this.hasSnowball = false;
		SnowballEntity snowball = new SnowballEntity(world, getX(), getEyeY(), getZ());
		snowball.setVelocity(target.getX() - getX(), target.getEyeY() - getEyeY(), target.getZ() - getZ(), 0.5f, 0);
		this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		world.spawnEntity(snowball);
	}

	@Override
	public void tick() {
		super.tick();
		if (world.isClient) {
			prevCollectingProgress = collectingProgress;
			if (isCollecting()) {
				collectingProgress++;
			} else {
				collectingProgress = 0;
				prevCollectingProgress = 0;
			}
		}
		tickHandSwing();
	}

	public float getCollectingProgress(float partialTicks) {
		return MathHelper.lerp(partialTicks, prevCollectingProgress, collectingProgress);
	}

	private static class ThrowSnowballGoal extends Goal {

		private GreedyGnomeEntity gnome;
		private PlayerEntity target;

		private ThrowSnowballGoal(GreedyGnomeEntity gnome) {
			this.gnome = gnome;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			target = gnome.world.getClosestPlayer(gnome, 7);
			return target != null && gnome.hasSnowball && gnome.getRandom().nextDouble() < 0.01;
		}

		@Override
		public boolean shouldContinue() {
			return gnome.hasSnowball && target != null;
		}

		@Override
		public void start() {
			if (target != null)
				gnome.throwSnowball(target);
		}

	}

	private static class CollectQuestItemGoal extends Goal {

		private GreedyGnomeEntity gnome;
		private Random rand;
		private ItemEntity target;
		private int collectingTimer;

		public CollectQuestItemGoal(GreedyGnomeEntity gnome) {
			this.gnome = gnome;
			this.rand = gnome.getRandom();
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return !getNearbyQuestItems().isEmpty();
		}

		@Override
		public void start() {
			List<ItemEntity> questItems = getNearbyQuestItems();
			if (!questItems.isEmpty()) {
				gnome.getNavigation().startMovingTo(questItems.get(0), (double) 1.2F);
			}
		}

		@Override
		public void stop() {
			gnome.setCollecting(false);
		}

		@Override
		public void tick() {
			List<ItemEntity> nearbyQuestItems = getNearbyQuestItems();
			if (nearbyQuestItems.isEmpty())
				return;

			ItemEntity nearbyQuestItem = nearbyQuestItems.get(0);
			gnome.getNavigation().startMovingTo(nearbyQuestItems.get(0), (double) 1.2F);

			if (nearbyQuestItem.squaredDistanceTo(gnome) > 4)
				return;

			if (nearbyQuestItem != target) {
				collectingTimer = 40;
			} else {
				if (collectingTimer-- < 0) {
					gnome.setTarget(null);
					gnome.setAttacker(null);
					gnome.setCollecting(false);
					if (target.getStack().getItem() == Items.GOLD_INGOT) {
						target.getStack().decrement(1);
						gnome.changeQuest();
					} else {
						ItemStack questStack = gnome.getQuest();
						Item questItem = questStack.getItem();
						int decrement = Math.min(questStack.getCount(), target.getStack().getCount());
						questStack.decrement(decrement);
						target.getStack().decrement(decrement);

						// Quest completed
						if (questStack.isEmpty()) {
							if (questItem == ModInit.GREEDY_GNOME_BUNDLE_ITEM) {
								GreedyGnomeEntity fromBundle = new GreedyGnomeEntity(gnome.world);
								Vec3d spawnPos = randomNearbyPos();
								fromBundle.updatePosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
								gnome.world.spawnEntity(fromBundle);
							} else {
								dropReward();
								if (questItem == Items.SNOWBALL)
									gnome.giveSnowball();
							}
							gnome.changeQuest();
						} else {
							gnome.setQuest(new ItemStack(questItem, questStack.getCount())); // Update quest to client
						}
					}
					target = null;
					return;
				} else {
					gnome.setCollecting(true);
				}
			}
			target = nearbyQuestItem;
		}

		private void dropReward() {
			for (int i = 0; i < 3; i++) {
				Vec3d pos = randomNearbyPos();
				gnome.world.spawnEntity(
						new ExperienceOrbEntity(gnome.world, pos.getX(), pos.getY(), pos.getZ(), rand.nextInt(5) + 5));
			}
			LookTargetUtil.give(gnome, new ItemStack(Items.GOLD_NUGGET, rand.nextInt(8) + 8),
					randomNearbyPos().add(0.0D, 1.0D, 0.0D));
		}

		private Vec3d randomNearbyPos() {
			return new Vec3d(gnome.getX() + rand.nextDouble() * 0.4 - 0.2, gnome.getY(),
					gnome.getZ() + rand.nextDouble() * 0.4 - 0.2);
		}

		private List<ItemEntity> getNearbyQuestItems() {
			return gnome.world.getEntitiesByClass(ItemEntity.class, gnome.getBoundingBox().expand(8.0D, 8.0D, 8.0D),
					e -> gnome.getQuest().getItem() == e.getStack().getItem()
							|| e.getStack().getItem() == Items.GOLD_INGOT);
		}

	}

	private static class Quest {
		private Item item;
		private int min;
		private int max;

		private Quest(Item item, int min, int max) {
			this.item = item;
			this.min = min;
			this.max = max;
		}

		private ItemStack createQuestInstance(Random random) {
			return new ItemStack(item, min == max ? min : random.nextInt(max - min) + min);
		}
	}
}
