package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils.getPowerFoodComponent;

@Mixin(Item.class)
public abstract class CustomEdibleItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void ssc$use(Level level, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = user.getItemInHand(hand);
        FoodProperties food = getPowerFoodComponent(user, stack);
        if (food == null) {
            return;
        }
        if (user.canEat(food.canAlwaysEat())) {
            user.startUsingItem(hand);
            cir.setReturnValue(InteractionResultHolder.consume(stack));
        } else {
            cir.setReturnValue(InteractionResultHolder.fail(stack));
        }
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void ssc$finishUsing(ItemStack stack, Level level, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof Player player) {
            FoodProperties food = getPowerFoodComponent(player, stack);
            if (food != null) {
                cir.setReturnValue(player.eat(level, stack, food));
            }
        }
    }
}
