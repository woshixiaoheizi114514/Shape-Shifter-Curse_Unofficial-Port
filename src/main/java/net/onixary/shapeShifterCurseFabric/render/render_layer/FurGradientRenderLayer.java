package net.onixary.shapeShifterCurseFabric.render.render_layer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * [未启用，勿再当作待办] 本分支已移除 Satin 依赖，原 Satin 的
 * {@code ShaderEffectManager} / {@code ManagedCoreShader} / {@code Uniform1f} /
 * {@code EntitiesPreRenderCallback} 均已删除，本类只剩一个 tick 计数器。
 *
 * <p>关键事实：fur gradient 整套在 1.21.1 / 1.21.11 / 26.1 三个分支上都**无调用点**
 * （{@code FurColorGradientRenderLayer.getFurLayer} 只有定义），从未接线启用。
 * 要真正恢复需要自定义 core shader，但那会与光影包（Iris 等）冲突，故不采用着色器方案
 * —— 详见记忆 {@code ssc-no-custom-shader}。如需恢复请先确认调用方与光影兼容性。</p>
 */
public abstract class FurGradientRenderLayer {

    private static int ticks;

    public static void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> ticks++);
    }
}
