package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.tacz.guns.api.event.common.GunMeleeEvent;
import com.tacz.guns.api.event.common.GunReloadEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.compat.playeranimator.animation.AnimationManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnimationManager.class, remap = false)
public class TacZ_Anim {
    @Inject(method = "onFire", at = @At("HEAD"), cancellable = true)
    private void onFire(GunShootEvent event, CallbackInfo ci) {
        if (event.getShooter() instanceof AbstractClientPlayer player) {
            IForm form = FormTextureUtils.getPlayerForm_Render(player);
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "onReload", at = @At("HEAD"), cancellable = true)
    private void onReload(GunReloadEvent event, CallbackInfo ci) {
        if (event.getEntity() instanceof AbstractClientPlayer player) {
            IForm form = FormTextureUtils.getPlayerForm_Render(player);
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "onMelee", at = @At("HEAD"), cancellable = true)
    private void onMelee(GunMeleeEvent event, CallbackInfo ci) {
        if (event.getShooter() instanceof AbstractClientPlayer player) {
            IForm form = FormTextureUtils.getPlayerForm_Render(player);
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
    // ⚠ 这里**没有**对 onDraw 的拦截，是有意为之：
    // TaCZ 的 onDraw(GunDrawEvent) 只调 stopAnimation(LOOP_UPPER/ONCE_UPPER/LOWER)，
    // 它是「清」入口、从不写动画。FERAL 时取消它等于主动删掉「切枪时清理」这条路径，
    // 反而让残留的 TaCZ 层更顽固。onFire/onReload/onMelee 才是写入口，故只拦它们。
}