package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.onixary.shapeShifterCurseFabric.networking.BytePayload;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModPacketsS2C.class, remap = false)
public class TacZ_AnimFormChange {
    @Inject(method = "receiveFormChange", at = @At("TAIL"))
    private static void ssc$clearTaczAnimationOnFeral(BytePayload payload, ClientPlayNetworking.Context ctx, CallbackInfo ci) {
        ctx.client().execute(() -> {
            Player player = ctx.client().player;
            if (!(player instanceof AbstractClientPlayer)) {
                return;
            }
            if (FormTextureUtils.getPlayerForm_Render(player).getBodyType() != PlayerFormBodyType.FERAL) {
                return;
            }
            PlayerAnimatorCompat.stopAllAnimation(player);
        });
    }
}
