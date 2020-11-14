package mod.vemerion.greedygnomes.entity;

import mod.vemerion.greedygnomes.ModInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class GreedyGnomeEntity extends PathAwareEntity {

	private static final TrackedData<ItemStack> QUEST = DataTracker.registerData(GreedyGnomeEntity.class,
			TrackedDataHandlerRegistry.ITEM_STACK);

	public GreedyGnomeEntity(EntityType<GreedyGnomeEntity> entityType, World world) {
		super(entityType, world);
	}

	public GreedyGnomeEntity(World world) {
		super(ModInit.GREEDY_GNOME, world);
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
	}

	@Override
	protected void initGoals() {
		goalSelector.add(1, new SwimGoal(this));
		goalSelector.add(5, new WanderAroundFarGoal(this, 1));
		goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		goalSelector.add(7, new LookAroundGoal(this));

	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		dataTracker.startTracking(QUEST, new ItemStack(Items.APPLE, 10));
	}

	public ItemStack getQuest() {
		return dataTracker.get(QUEST);
	}

}
