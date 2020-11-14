package mod.vemerion.greedygnomes;

import mod.vemerion.greedygnomes.entity.GreedyGnomeEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModInit implements ModInitializer {

	public static final String MODID = "greedy-gnomes";

	public static final EntityType<GreedyGnomeEntity> GREEDY_GNOME = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(MODID, "greedy_gnome"),
			FabricEntityTypeBuilder.<GreedyGnomeEntity>create(SpawnGroup.CREATURE, GreedyGnomeEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 1.75f)).build());

	@Override
	public void onInitialize() {
        FabricDefaultAttributeRegistry.register(GREEDY_GNOME, GreedyGnomeEntity.createAttributes());

	}

}
