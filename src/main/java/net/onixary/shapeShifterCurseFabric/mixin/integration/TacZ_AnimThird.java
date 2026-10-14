package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.tacz.guns.client.animation.third.InnerThirdPersonManager;
import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InnerThirdPersonManager.class)
public class TacZ_AnimThird {
    @Inject(method = "setRotationAnglesHead", at = @At("HEAD"), cancellable = true)
    private static void setRotationAnglesHead(LivingEntity entityIn, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head, float limbSwingAmount, CallbackInfo ci) {
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        if (!(entityIn instanceof Player player)) {
            return;
        }
        IForm form = FormTextureUtils.getPlayerForm_Render(player);
        if (form.getBodyType() != PlayerFormBodyType.FERAL) {
            return;
        }
        PlayerAnimatorCompat.stopAllAnimation(entityIn);
        ci.cancel();
    }
}
