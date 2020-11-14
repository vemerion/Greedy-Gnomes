package mod.vemerion.greedygnomes;

import mod.vemerion.greedygnomes.renderer.GreedyGnomeRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class ClientModInit implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(ModInit.GREEDY_GNOME, (dispatcher, context) -> {
			return new GreedyGnomeRenderer(dispatcher);
		});
	}
}