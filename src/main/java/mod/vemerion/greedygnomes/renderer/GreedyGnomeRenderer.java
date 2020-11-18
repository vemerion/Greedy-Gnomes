package mod.vemerion.greedygnomes.renderer;

import java.awt.Color;

import mod.vemerion.greedygnomes.ModInit;
import mod.vemerion.greedygnomes.entity.GreedyGnomeEntity;
import mod.vemerion.greedygnomes.model.GreedyGnomeModel;
import mod.vemerion.greedygnomes.model.QuestBubbleModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

public class GreedyGnomeRenderer extends MobEntityRenderer<GreedyGnomeEntity, GreedyGnomeModel> {
	private static final Identifier TEXTURE = new Identifier(ModInit.MODID, "textures/entity/greedy_gnome_model.png");
	private static final Identifier BUBBLE_TEXTURE = new Identifier(ModInit.MODID, "textures/entity/bubble.png");
	private static final QuestBubbleModel BUBBLE = new QuestBubbleModel();
	private static final int BLACK = Color.BLACK.getRGB();
	private static final int TRANSPARENT = 0;

	public GreedyGnomeRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new GreedyGnomeModel(), 0.2f);

		addFeature(new HeldItemFeatureRenderer<GreedyGnomeEntity, GreedyGnomeModel>(this) {
			@Override
			public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
					GreedyGnomeEntity livingEntity, float f, float g, float h, float j, float k, float l) {
				if (livingEntity.isAttacking())
					super.render(matrixStack, vertexConsumerProvider, i, livingEntity, f, g, h, j, k, l);
			}
		});
	}

	@Override
	public void render(GreedyGnomeEntity mobEntity, float f, float g, MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider, int i) {
		renderQuestBubble(mobEntity, matrixStack, vertexConsumerProvider, i);
		super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	private void renderQuestBubble(GreedyGnomeEntity mobEntity, MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider, int i) {
		ItemStack questStack = mobEntity.getQuest();
		if (MinecraftClient.getInstance().player.squaredDistanceTo(mobEntity) < 25) {
			matrixStack.push();
			matrixStack.translate(0.0D, mobEntity.getHeight() + 0.5f, 0.0D);
			matrixStack
					.multiply(new Quaternion(0, dispatcher.getRotation().getY(), 0, dispatcher.getRotation().getW()));
			matrixStack.push();
			matrixStack.scale(0.5f, 0.5f, 0.5f);
			matrixStack.translate(0.4, 0.35, 0);
			MinecraftClient.getInstance().getItemRenderer().renderItem(questStack, Mode.GUI, i,
					OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);

			matrixStack.push();
			matrixStack.scale(-0.07f, -0.07f, 0.07f);
			matrixStack.translate(12, -2.5, -0.01);
			Matrix4f matrix4f = matrixStack.peek().getModel();
			TextRenderer textRenderer = this.getFontRenderer();
			String text = String.valueOf(questStack.getCount());
			textRenderer.draw(text, -textRenderer.getWidth(text) / 2, 0, BLACK, false, matrix4f, vertexConsumerProvider,
					false, TRANSPARENT, i);

			matrixStack.pop();
			matrixStack.pop();
			matrixStack.scale(-1, -1, 1);

			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(BUBBLE.getLayer(BUBBLE_TEXTURE));
			BUBBLE.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
			matrixStack.pop();
		}
	}

	@Override
	public Identifier getTexture(GreedyGnomeEntity entity) {
		return TEXTURE;
	}

}
