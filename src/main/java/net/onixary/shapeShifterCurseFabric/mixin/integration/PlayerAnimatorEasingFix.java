package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = dev.kosmx.playerAnim.core.util.Easing.class, remap = false)
public class PlayerAnimatorEasingFix {

    @ModifyReturnValue(method = "catmullRom(F)F", at = @At("RETURN"))
    private static float ssc$fixCatmullRom(float original, float n) {
        return 0.5f * (n + 3.0f * n * n - 2.0f * n * n * n);
    }
}
