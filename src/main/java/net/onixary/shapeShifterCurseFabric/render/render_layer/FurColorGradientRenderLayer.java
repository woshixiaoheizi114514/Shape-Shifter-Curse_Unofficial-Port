package net.onixary.shapeShifterCurseFabric.render.render_layer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4f;

public abstract class FurColorGradientRenderLayer {
    public static RenderType getFurLayer(ResourceLocation texture, Vector4f startColor, Vector4f endColor) {
        return RenderType.entityTranslucent(texture);
    }
}
