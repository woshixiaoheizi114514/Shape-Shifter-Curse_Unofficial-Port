package net.onixary.shapeShifterCurseFabric.render.render_layer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4f;

/**
 * [未启用，勿再当作待办] 本分支已移除 Satin 依赖，原先那条「自定义 core shader + ShaderStateShard」的
 * 路线（依赖 {@code ShapeShifterCurseFabricClient.getFurGradientShader()}）已整体撤掉，
 * 此法退化为**普通半透明渲染**（颜色渐变效果不可用）。
 *
 * <p>关键事实：三个分支上 {@code getFurLayer} 都只有定义、**没有任何调用点**，整套从未接线启用；
 * 且恢复需自定义 core shader，会与光影包（Iris 等）冲突，故不采用着色器方案。
 * 如需恢复请先确认调用方与光影兼容性。做法与 {@code ver/1.21.11} 分支对齐。</p>
 *
 * <p>{@code startColor} / {@code endColor} 两个形参**保留但被忽略** —— 沿用上游签名，
 * 将来若要恢复渐变，调用方不必跟着改。</p>
 */
public abstract class FurColorGradientRenderLayer {
    public static RenderType getFurLayer(ResourceLocation texture, Vector4f startColor, Vector4f endColor) {
        return RenderType.entityTranslucent(texture);
    }
}
