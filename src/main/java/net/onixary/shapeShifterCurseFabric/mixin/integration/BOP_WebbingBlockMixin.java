package net.onixary.shapeShifterCurseFabric.mixin.integration;

import biomesoplenty.block.WebbingBlock;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.onixary.shapeShifterCurseFabric.additional_power.SlowdownPercentPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WebbingBlock.class)
public class BOP_WebbingBlockMixin {
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void ssc$entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            List<SlowdownPercentPower> slowdownPower = PowerHolderComponent.getPowers(player, SlowdownPercentPower.class);
            float slowdownPercent = 1.0f;
            for (SlowdownPercentPower power : slowdownPower) {
                slowdownPercent *= power.Multiplier;
            }
            player.setDeltaMovement(player.getDeltaMovement().multiply(
                    1D - (0.375D * slowdownPercent),
                    1D - (0.25D * slowdownPercent),
                    1D - (0.375D * slowdownPercent)));
            ci.cancel();
        }
    }
}
