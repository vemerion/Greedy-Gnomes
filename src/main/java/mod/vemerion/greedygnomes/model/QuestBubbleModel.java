package mod.vemerion.greedygnomes.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Created using Tabula 8.0.0
 */
public class QuestBubbleModel extends Model {
    public ModelPart bubble;

    public QuestBubbleModel() {
    	super(RenderLayer::getEntityCutoutNoCull);
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.bubble = new ModelPart(this, 0, 0);
        this.bubble.setPivot(0.0F, 0.0F, 0.0F);
        this.bubble.addCuboid(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.bubble).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }
}
