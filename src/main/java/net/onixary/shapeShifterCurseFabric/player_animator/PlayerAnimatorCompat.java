package net.onixary.shapeShifterCurseFabric.player_animator;

import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.math.Vec3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PlayerAnimator（{@code dev.kosmx.playerAnim}）兼容层。
 *
 * <h2>为什么要它</h2>
 * SSCU 的形态动画跑在 PAL 上，而第三方动作 mod（TacZ、Emotecraft 等）跑在 PlayerAnimator 上。
 * 两个库各有自己的 {@code AnimationStack}，互不相通——双方各自去写 vanilla {@code PlayerModel}
 * 的同一批 {@code ModelPart}，结果是「一方覆盖另一方」，而不是按层优先级叠加。
 * <p>
 * 本类让 SSCU 在装了 PlayerAnimator 时把形态动画也放进 <b>PA 的 stack</b>（优先级 1）。
 * PA 的 {@code AnimationStack.get3DTransform} 是链式覆盖：逐层把上一层的输出当作
 * {@code value0} 传下去，而 {@code KeyframeAnimationPlayer} 对**未定义骨骼 / 未启用的轴**
 * 原样返回 {@code value0}。所以 SSCU 低优先级打底、第三方 mod 用更高优先级只覆盖它自己
 * 定义的那几根骨骼——这正是「兼容」的实现机制。
 *
 * <h2>类加载安全（改这个文件前务必读）</h2>
 * PlayerAnimator 是<b>可选</b>依赖，本类必须能在它缺席时被安全加载：
 * <ul>
 *   <li>字段只有 {@code boolean}；<b>方法签名只用 PAL 类型 / {@code Object} / 基础类型</b>
 *       ——PAL 在 {@code fabric.mod.json} 的 {@code depends} 里，必然存在。</li>
 *   <li>PA 类型<b>只出现在方法体内</b>，且一律写全限定名。JVM 对方法体内的符号引用是
 *       <b>首次执行时才解析</b>，所以只要每个方法都被 {@link #available()} 保护，
 *       PA 缺席时不会 {@code NoClassDefFoundError}。</li>
 *   <li>容器用 {@code Object} 传递（实际类型是 {@code ModifierLayer<IAnimation>}）。不要把它
 *       声明成 PA 类型的字段——字段类型与 StackMapTable 可能在类加载/验证阶段就被解析。</li>
 * </ul>
 *
 * <h2>实现来源</h2>
 * 照搬 SSCU 迁移到 PAL 之前的版本（commit {@code 24786162^}：当时是 PA {@code 2.0.4+1.21.1}，
 * Yarn 映射），以保证行为与上游一致；名称已转成 Mojmap。
 * 其中 {@code disableArmAnimations()} / {@code armAnimationsEnabled} 是死代码（无任何外部调用者），
 * 未予恢复。
 */
public final class PlayerAnimatorCompat {
    private PlayerAnimatorCompat() {}

    private static final boolean AVAILABLE = detect();

    private static boolean detect() {
        try {
            // 专用服务端固定走 PAL：AnimationHolder 双端都会构造（AnimStateControllerDP 的构造函数
            // 由 AnimRegistries 静态注册触发），而 PA 的 PlayerAnimationRegistry 标了 @Environment(CLIENT)。
            return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                    && FabricLoader.getInstance().isModLoaded("playeranimator");
        } catch (Throwable t) {
            // loader 尚未就绪等极端情况：退回 PAL 路径，不能让兼容层本身成为崩溃源
            return false;
        }
    }

    /**
     * PlayerAnimator 是否可用（客户端且已安装）。为 true 时 SSCU 的动画全部改走 PA 路径。
     * <p>
     * 所有会引用 PA 类型的方法都<b>必须</b>先过这个判断再调用。
     */
    public static boolean available() {
        return AVAILABLE;
    }

    // ------------------------------------------------------------------
    // 骨骼查询
    // ------------------------------------------------------------------

    /**
     * 查询 PA 动画栈上某根骨骼的变换（{@link net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem#getPlayerBone3DTransform} 的 PA 分支）。
     *
     * @param rawBoneName 动画里的<b>原始骨骼名</b>，直接用 camelCase，<b>不要归一化</b>。
     *                    PA 的 {@code GeckoLibSerializer} 对骨骼 key 做 {@code snake2Camel}，
     *                    而 SSCU 的动画资源本来就是 camelCase（{@code rightArm} / {@code bipedRightHindLeg}…），
     *                    所以那是恒等映射。这与 PAL 侧恰好相反——PAL 要 snake_case，
     *                    因此归一化只发生在 PAL 分支内部（见 {@code AnimSystem.normalizeAnimBoneName}）。
     * @return PA 无数据时返回 {@code defaultValue}（未启用、无此骨骼、或类型不支持）
     */
    public static @NotNull Vec3f getBoneTransform(@NotNull Player player, @NotNull String rawBoneName,
                                                  @NotNull TransformType type, @NotNull Vec3f defaultValue) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return defaultValue;
        if (!(clientPlayer instanceof dev.kosmx.playerAnim.impl.IAnimatedPlayer paPlayer)) return defaultValue;

        dev.kosmx.playerAnim.impl.animation.AnimationApplier applier = paPlayer.playerAnimator_getAnimation();
        if (applier == null || !applier.isActive()) return defaultValue;

        dev.kosmx.playerAnim.api.TransformType paType = switch (type) {
            case POSITION -> dev.kosmx.playerAnim.api.TransformType.POSITION;
            case ROTATION -> dev.kosmx.playerAnim.api.TransformType.ROTATION;
            case SCALE -> dev.kosmx.playerAnim.api.TransformType.SCALE;
            case BEND -> dev.kosmx.playerAnim.api.TransformType.BEND;
        };

        dev.kosmx.playerAnim.core.util.Vec3f result = applier.get3DTransform(
                rawBoneName, paType,
                new dev.kosmx.playerAnim.core.util.Vec3f(defaultValue.x(), defaultValue.y(), defaultValue.z()));
        return new Vec3f(result.getX(), result.getY(), result.getZ());
    }

    // ------------------------------------------------------------------
    // 播放驱动
    // ------------------------------------------------------------------

    /**
     * 为玩家建一个 PA 动画层并挂到 PA 的 stack 上，在玩家的 {@code <init>} 里调用一次。
     *
     * @return 该玩家的层容器（{@code ModifierLayer<IAnimation>}，以 {@code Object} 返回）；不可用时为 null
     */
    public static @Nullable Object createLayer(@NotNull AbstractClientPlayer player) {
        dev.kosmx.playerAnim.api.layered.ModifierLayer<dev.kosmx.playerAnim.api.layered.IAnimation> container =
                new dev.kosmx.playerAnim.api.layered.ModifierLayer<>();
        // 优先级 1：低于第三方动作 mod 的层，使其能覆盖 SSCU 的骨骼
        dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess.getPlayerAnimLayer(player).addAnimLayer(1, container);
        container.setAnimation(null);
        return container;
    }

    /**
     * 带淡入地播放动画。
     * <p>
     * 保持与 PAL 路径「先加速率 modifier、再 replaceAnimationWithFade」同样的顺序。
     * 注意 PA 的 {@code removeModifier(int)} 是裸的 {@code List.remove}，<b>容器为空时会越界</b>，
     * 所以 {@code needsCleanup} 这个状态必须由调用方跨 tick 记住（照搬 {@code 24786162^} 的 {@code modified}）。
     *
     * @param needsCleanup 上一次调用后容器里是否已经存在 modifier
     * @return 本次调用后容器里是否已存在 modifier（供调用方存回）
     */
    public static boolean play(@NotNull Object containerObj, @NotNull ResourceLocation animationID,
                               float speed, int fade, boolean needsCleanup) {
        dev.kosmx.playerAnim.api.IPlayable playable =
                dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry.getAnimation(animationID);
        if (playable == null) return needsCleanup;

        var container = (dev.kosmx.playerAnim.api.layered.ModifierLayer<dev.kosmx.playerAnim.api.layered.IAnimation>) containerObj;
        if (needsCleanup) container.removeModifier(0);
        container.addModifierBefore(new dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier(speed));
        // 缓动先固定 LINEAR：PA 侧的最短弧淡入修复留作后续任务（见计划）。
        container.replaceAnimationWithFade(
                dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier.standardFadeIn(
                        fade, dev.kosmx.playerAnim.core.util.Ease.LINEAR),
                playable.playAnimation());
        container.setupAnim(1.0f / 20.0f);
        return true;
    }

    /**
     * 硬切动画（对应 {@code AnimationHolder#isSkipFade()}）。
     * <p>
     * 与 {@link #play} 一致，PAL 路径的 skipFade 分支也不做变速，所以这里只清掉旧的速率 modifier。
     *
     * @return 本次调用后容器里是否已存在 modifier——恒为 false，因为没有加任何 modifier
     */
    public static boolean playImmediate(@NotNull Object containerObj, @NotNull ResourceLocation animationID,
                                        boolean needsCleanup) {
        dev.kosmx.playerAnim.api.IPlayable playable =
                dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry.getAnimation(animationID);
        if (playable == null) return needsCleanup;

        var container = (dev.kosmx.playerAnim.api.layered.ModifierLayer<dev.kosmx.playerAnim.api.layered.IAnimation>) containerObj;
        if (needsCleanup) container.removeModifier(0);
        container.setAnimation(playable.playAnimation());
        container.setupAnim(1.0f / 20.0f);
        return false;
    }

    /** 清空当前动画（对应 {@code AnimSystem#getAnimation()} 返回 null 的情况）。 */
    public static void stop(@NotNull Object containerObj) {
        // 收窄写成方法体内的局部强转（而不是抽一个返回 PA 类型的私有方法）：
        // 这样 PA 类型严格只出现在方法体内，见类注释「类加载安全」。
        ((dev.kosmx.playerAnim.api.layered.ModifierLayer<dev.kosmx.playerAnim.api.layered.IAnimation>) containerObj)
                .setAnimation(null);
    }
}
