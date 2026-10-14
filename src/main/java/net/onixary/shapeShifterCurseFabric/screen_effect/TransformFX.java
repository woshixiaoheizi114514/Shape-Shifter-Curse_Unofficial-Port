package net.onixary.shapeShifterCurseFabric.screen_effect;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

/**
 * 变身时的黑屏/暗角渐变。
 *
 * <p>本分支已移除 Satin 依赖，原 Satin 的 {@code ManagedShaderEffect} / {@code ShaderEffectManager} /
 * {@code Uniform1f} / {@code ShaderEffectRenderCallback} 已删除，不再走 post-processing 着色器
 * —— post shader（Satin / PostChain）与光影包（Iris 等）兼容极差。</p>
 *
 * <p>实际视觉改由无 shader 的 {@link TransformOverlay} 承担：进入渐变由 {@code TransformManager}
 * 每帧驱动 strength，退出渐变在 {@code TransformOverlay.renderHud} 里每帧衰减
 * （{@code setEnableOverlay(false)} 后平滑淡出）。本类只保留 begin/end 的静态状态与 tick 计数，
 * 供 {@code ModPacketsS2C} 调用；{@code TRANSFORM_EFFECT_SHADER_ID} 保留仅作记录。</p>
 */
public final class TransformFX implements ClientTickEvents.EndTick {
    public static final ResourceLocation TRANSFORM_EFFECT_SHADER_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "shaders/post/transform_effect.json");

    public static final TransformFX INSTANCE = new TransformFX();
    private final Minecraft mc = Minecraft.getInstance();

    private static float transformEffectDuration = 0.0f;
    private static boolean doEffectIn = false;
    private static boolean doEffectOut = false;

    private int ticks = 0;

    public void registerCallbacks() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        ++ticks;
    }

    public static void beginTransformEffect() {
        doEffectIn = true;
        doEffectOut = false;
        transformEffectDuration = StaticParams.TRANSFORM_FX_DURATION_IN;
    }

    public static void endTransformEffect() {
        doEffectIn = false;
        doEffectOut = true;
        transformEffectDuration = StaticParams.TRANSFORM_FX_DURATION_OUT;
    }

}
