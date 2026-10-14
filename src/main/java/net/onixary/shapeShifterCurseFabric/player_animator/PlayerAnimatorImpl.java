package net.onixary.shapeShifterCurseFabric.player_animator;

import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.math.Vec3f;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlayerAnimatorImpl implements PlayerAnimatorBridge {

    @Override
    public @Nullable Object createLayer(@NotNull AbstractClientPlayer player) {
        dev.kosmx.playerAnim.api.layered.ModifierLayer<dev.kosmx.playerAnim.api.layered.IAnimation> container =
                new dev.kosmx.playerAnim.api.layered.ModifierLayer<>();
        dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess.getPlayerAnimLayer(player).addAnimLayer(1, container);
        container.setAnimation(null);
        return container;
    }

    @Override
    public boolean play(@NotNull Object containerObj, @NotNull ResourceLocation animationID,
                        float speed, int fade, boolean needsCleanup) {
        dev.kosmx.playerAnim.api.IPlayable playable =
                dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry.getAnimation(animationID);
        if (playable == null) return needsCleanup;

        var container = (dev.kosmx.playerAnim.api.layered.ModifierLayer<dev.kosmx.playerAnim.api.layered.IAnimation>) containerObj;
        if (needsCleanup) container.removeModifier(0);
        container.addModifierBefore(new dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier(speed));
        container.replaceAnimationWithFade(
                dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier.standardFadeIn(
                        fade, dev.kosmx.playerAnim.core.util.Ease.LINEAR),
                playable.playAnimation());
        container.setupAnim(1.0f / 20.0f);
        return true;
    }

    @Override
    public boolean playImmediate(@NotNull Object containerObj, @NotNull ResourceLocation animationID,
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

    @Override
    public void stop(@NotNull Object containerObj) {
        ((dev.kosmx.playerAnim.api.layered.ModifierLayer<dev.kosmx.playerAnim.api.layered.IAnimation>) containerObj)
                .setAnimation(null);
    }

    @Override
    public @NotNull Vec3f getBoneTransform(@NotNull Player player, @NotNull String rawBoneName,
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

    @Override
    public boolean isFirstPersonPass() {
        return dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode.isFirstPersonPass();
    }

    @Override
    public @Nullable FirstPersonArms getFirstPersonArms(@NotNull Player player) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return null;
        if (!(clientPlayer instanceof dev.kosmx.playerAnim.impl.IAnimatedPlayer paPlayer)) return null;

        dev.kosmx.playerAnim.impl.animation.AnimationApplier applier = paPlayer.playerAnimator_getAnimation();
        if (applier == null || !applier.isActive()) return null;

        dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration config =
                applier.getFirstPersonConfiguration();
        return new FirstPersonArms(config.isShowLeftArm(), config.isShowRightArm());
    }
}
