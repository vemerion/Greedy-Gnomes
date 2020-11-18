package mod.vemerion.greedygnomes;

import java.awt.Color;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mod.vemerion.greedygnomes.entity.GreedyGnomeEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModInit implements ModInitializer {

	public static final String MODID = "greedy-gnomes";

	public static final EntityType<GreedyGnomeEntity> GREEDY_GNOME = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(MODID, "greedy_gnome"),
			FabricEntityTypeBuilder.<GreedyGnomeEntity>create(SpawnGroup.CREATURE, GreedyGnomeEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 1.75f)).build());

	public static final Item GREEDY_GNOME_BUNDLE_ITEM = Registry.register(Registry.ITEM,
			new Identifier(MODID, "greedy_gnome_bundle_item"),
			new Item(new Item.Settings().group(ItemGroup.MISC).maxCount(1)));

	public static final Item GREEDY_GNOME_SPAWN_EGG_ITEM = Registry.register(Registry.ITEM,
			new Identifier(MODID, "greedy_gnome_spawn_egg_item"),
			new SpawnEggItem(GREEDY_GNOME, new Color(14, 99, 21).getRGB(), new Color(126, 42, 33).getRGB(),
					new Item.Settings().group(ItemGroup.MISC)));

	private static final Set<Identifier> LOOT_TABLES = ImmutableSet.of(
			new Identifier("minecraft", "chests/abandoned_mineshaft"),
			new Identifier("minecraft", "chests/simple_dungeon"),
			new Identifier("minecraft", "chests/stronghold_corridor"));

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(GREEDY_GNOME, GreedyGnomeEntity.createAttributes());

		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
			if (LOOT_TABLES.contains(id)) {
				FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1))
						.withCondition(RandomChanceLootCondition.builder(0.2f).build())
						.withEntry(ItemEntry.builder(GREEDY_GNOME_BUNDLE_ITEM).build());
				supplier.pool(builder);
			}
		});
	}

}
