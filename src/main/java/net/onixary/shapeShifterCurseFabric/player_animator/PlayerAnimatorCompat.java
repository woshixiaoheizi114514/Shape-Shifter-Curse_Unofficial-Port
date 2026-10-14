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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerAnimatorCompat {
    private PlayerAnimatorCompat() {}

    private static final Logger LOGGER = LoggerFactory.getLogger("shape-shifter-curse/playeranimator");

    private static final String[] REQUIRED_CLASSES = {
            "dev.kosmx.playerAnim.api.layered.ModifierLayer",
            "dev.kosmx.playerAnim.api.layered.IAnimation",
            "dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier",
            "dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier",
            "dev.kosmx.playerAnim.api.IPlayable",
            "dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess",
            "dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry",
            "dev.kosmx.playerAnim.impl.IAnimatedPlayer",
            "dev.kosmx.playerAnim.impl.animation.AnimationApplier",
            "dev.kosmx.playerAnim.core.util.Ease",
    };

    private static final String IMPL_CLASS =
            "net.onixary.shapeShifterCurseFabric.player_animator.PlayerAnimatorImpl";

    private static final @Nullable PlayerAnimatorBridge BRIDGE = loadBridge();

    private static @Nullable PlayerAnimatorBridge loadBridge() {
        try {
            if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return null;
            ClassLoader loader = PlayerAnimatorCompat.class.getClassLoader();
            for (String className : REQUIRED_CLASSES) {
                Class.forName(className, false, loader);
            }
            PlayerAnimatorBridge bridge =
                    (PlayerAnimatorBridge) Class.forName(IMPL_CLASS, true, loader)
                            .getDeclaredConstructor().newInstance();
            LOGGER.info("[SSC] PlayerAnimator 已就位，形态动画将放入 PA 的 stack（与第三方动作 mod 按层混合）");
            return bridge;
        } catch (Throwable t) {
            LOGGER.info("[SSC] 未启用 PlayerAnimator 路径（{}），形态动画使用 PAL。", t.toString());
            return null;
        }
    }

    public static boolean available() {
        return BRIDGE != null;
    }

    public static @NotNull Vec3f getBoneTransform(@NotNull Player player, @NotNull String rawBoneName,
                                                  @NotNull TransformType type, @NotNull Vec3f defaultValue) {
        if (BRIDGE == null) return defaultValue;
        return BRIDGE.getBoneTransform(player, rawBoneName, type, defaultValue);
    }

    public static @Nullable Object createLayer(@NotNull AbstractClientPlayer player) {
        if (BRIDGE == null) return null;
        return BRIDGE.createLayer(player);
    }

    public static boolean play(@NotNull Object containerObj, @NotNull ResourceLocation animationID,
                               float speed, int fade, boolean needsCleanup) {
        if (BRIDGE == null) return needsCleanup;
        return BRIDGE.play(containerObj, animationID, speed, fade, needsCleanup);
    }

    public static boolean playImmediate(@NotNull Object containerObj, @NotNull ResourceLocation animationID,
                                        boolean needsCleanup) {
        if (BRIDGE == null) return needsCleanup;
        return BRIDGE.playImmediate(containerObj, animationID, needsCleanup);
    }

    public static void stop(@NotNull Object containerObj) {
        if (BRIDGE == null) return;
        BRIDGE.stop(containerObj);
    }

    public static boolean isFirstPersonPass() {
        return BRIDGE != null && BRIDGE.isFirstPersonPass();
    }

    public static @Nullable FirstPersonArms getFirstPersonArms(@NotNull Player player) {
        if (BRIDGE == null) return null;
        return BRIDGE.getFirstPersonArms(player);
    }
}
