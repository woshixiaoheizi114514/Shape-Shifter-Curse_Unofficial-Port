package net.onixary.shapeShifterCurseFabric.player_animator;

/**
 * 第一人称下「是否渲染左右手臂」的配置，来自 PlayerAnimator 的
 * {@code dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration}。
 *
 * <p>存在的理由同 {@link PlayerAnimatorBridge}：{@link PlayerAnimatorCompat} 是给动画渲染层
 * （{@code FormRenderFeature}）用的门面，而它的字节码里不能出现任何 {@code dev.kosmx} 类型——
 * PA 是可选依赖，一旦出现类引用，纯 PAL 环境会 {@code NoClassDefFoundError}。
 * 所以把两 bit 配置摘成这个纯 Java record 再往外传。</p>
 *
 * <p>注意 PA 的 {@code FirstPersonConfiguration} 两个 arm 字段<b>默认都是 false</b>
 * （即默认不显示手臂），这跟「没装 PA 时」的缺省行为要区分开：那种情况下本类实例是 {@code null}，
 * 调用方应按「不做第一人称裁剪」处理。</p>
 *
 * @param showLeft  是否渲染左臂
 * @param showRight 是否渲染右臂
 */
public record FirstPersonArms(boolean showLeft, boolean showRight) {
}
