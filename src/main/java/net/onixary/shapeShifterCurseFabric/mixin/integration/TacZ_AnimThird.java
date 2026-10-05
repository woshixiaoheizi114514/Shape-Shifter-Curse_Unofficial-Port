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

/** 目标 TaCZ 版本：1.21.1-0.7.1-forge1.1.8-hotfix2（签名若变会在此 mixin 应用时报错）。 */
@Mixin(InnerThirdPersonManager.class)
public class TacZ_AnimThird {
    @Inject(method = "setRotationAnglesHead", at = @At("HEAD"), cancellable = true)
    private static void setRotationAnglesHead(LivingEntity entityIn, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head, float limbSwingAmount, CallbackInfo ci) {
        // 与原方法一致的暂停守卫（暂停时继续计算会 StackOverflow）
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
        // ⚠ 本方法同时是 TaCZ 唯一的「写」入口和唯一的按帧「清」入口：
        //     无枪 / 睡觉 / 爬梯 / 游泳 / 鞘翅 → PlayerAnimatorCompat.stopAllAnimation（清）
        //     否则                            → playAnimation / playVanillaAnimation（写）
        // 若直接整体 cancel，「清」会被一起禁掉 —— TaCZ 的 4 个 PA 层优先级为 93/94/95/96，
        // 恒高于 SSC 形态动画层（1），一旦变身前被写过就再也没有代码去释放它，
        // 残留层会盖住 FERAL 动画（表现为「所有动画卡在 TaCZ 动画上，重进存档才恢复」）。
        // 因此这里先主动清一次（含只有 stopAllAnimation 能清的 ROTATION 层），再取消本次调用以抑制「写」。
        // 每帧重复调用是幂等的：replaceAnimationWithFade 在 animation 已为 null 后不再叠加 fade。
        PlayerAnimatorCompat.stopAllAnimation(entityIn);
        ci.cancel();
    }
}
