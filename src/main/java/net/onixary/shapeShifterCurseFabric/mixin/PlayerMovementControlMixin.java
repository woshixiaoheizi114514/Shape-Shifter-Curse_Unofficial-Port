package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnJumpPower;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import net.onixary.shapeShifterCurseFabric.additional_power.JumpEventCondition;
import net.onixary.shapeShifterCurseFabric.additional_power.SlowdownPercentPower;
import net.onixary.shapeShifterCurseFabric.additional_power.SneakingJumpClashPower;
import net.onixary.shapeShifterCurseFabric.additional_power.SprintingStateTracker;
import net.onixary.shapeShifterCurseFabric.networking.BytePayload;
import net.onixary.shapeShifterCurseFabric.networking.ModPackets;
import net.onixary.shapeShifterCurseFabric.util.Interface.IMoveController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Player.class)
public class PlayerMovementControlMixin implements IMoveController {

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void preventTravelWhenAttached(Vec3 movementInput, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        // 添加空值检查
        PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
        if (component == null) {
            return; // 组件未初始化，跳过处理
        }

        BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                .stream()
                .filter(BatBlockAttachPower::isAttached)
                .findFirst()
                .orElse(null);

        if (attachPower != null) {
            // 完全取消移动，类似蜂蜜块的效果
            player.setDeltaMovement(0, 0, 0);
            ci.cancel();
        }
    }

    @Inject(method = "getSpeed()F", at = @At("RETURN"), cancellable = true)
    private void zeroMovementSpeedWhenAttached(CallbackInfoReturnable<Float> cir) {
        if (noMoveTick > 0) {
            cir.setReturnValue(0.0f);
        }
        Player player = (Player) (Object) this;

        // 添加空值检查
        PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
        if (component == null) {
            return; // 组件未初始化，跳过处理
        }

	    PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
			    .stream()
			    .filter(BatBlockAttachPower::isAttached)
			    .findFirst().ifPresent(attachPower -> cir.setReturnValue(0.0f));
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void handleJump(CallbackInfo ci) {
        Player player = (Player) (Object) this;

        // 添加空值检查
        PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
        if (component == null) {
            return; // 组件未初始化，跳过处理
        }

        BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                .stream()
                .filter(BatBlockAttachPower::isAttached)
                .findFirst()
                .orElse(null);

        if (attachPower != null) {
            // 处理跳跃取消吸附
            if (player.level().isClientSide()) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                ClientPlayNetworking.send(new BytePayload(BytePayload.id(ModPackets.JUMP_DETACH_REQUEST_ID),  buf));
            }
            ci.cancel();
        }

        // handle jump_event condition
        JumpEventCondition.setJumping(player, true);

        if (player.level().isClientSide()) {
            // 客户端：发包通知服务端。
            // ⚠ 注意这条路径在普通跳跃时基本走不到 —— LocalPlayer 里唯一的 jumpFromGround() 调用点在
            // 「切换飞行能力」分支内（外层 if (abilities.mayfly)），地面起跳并不经过它。
            FriendlyByteBuf buf = PacketByteBufs.create();
            ClientPlayNetworking.send(new BytePayload(BytePayload.id(ModPackets.JUMP_EVENT_ID), buf));
        } else {
            // 服务端权威执行，不要依赖 JUMP_EVENT 包往返。
            // 服务端 jumpFromGround 由 ServerGamePacketListenerImpl 在处理 ServerboundMovePlayerPacket 时调用
            //（以 player.onGround() 为真为前提），此刻玩家仍在地面，entity_condition 里的 apoli:on_block 能通过。
            // 若只靠「客户端发包 → 服务端收包后执行」：包到达时跳跃已被应用、玩家已离地，on_block 必然为假，
            // ActionOnJumpPower 永不执行 —— 表现为跳跃技能的粒子/音效/加速全部消失（SneakingJumpClashPower 同理）。
            PowerHolderComponent.getPowers(player, ActionOnJumpPower.class).forEach(ActionOnJumpPower::executeAction);
            PowerHolderComponent.getPowers(player, SneakingJumpClashPower.class).forEach(sneakingJumpClashPower -> sneakingJumpClashPower.jumpTicks = 5);
        }
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void preventElytraCheckWhenAttached(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;

        // 添加空值检查
        PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
        if (component == null) {
            return; // 组件未初始化，跳过处理
        }

        BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                .stream()
                .filter(BatBlockAttachPower::isAttached)
                .findFirst()
                .orElse(null);

        if (attachPower != null) {

            if (player.level().isClientSide()) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                ClientPlayNetworking.send(new BytePayload(BytePayload.id(ModPackets.JUMP_DETACH_REQUEST_ID),  buf));
            }

            // 重置鞘翅相关标志
            player.stopFallFlying();
            // 强制设置为在地面上，这样空格键就不会触发鞘翅
            player.setOnGround(true);
            // 取消鞘翅检测
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void trackSprintingState(CallbackInfo ci) {
        Player player = (Player) (Object) this;

        boolean wasSprintingLastTick = SprintingStateTracker.wasSprintingLastTick(player);
        boolean isCurrentlySprinting = player.isSprinting();
        boolean isCurrentlySneaking = player.isShiftKeyDown();

        // 先更新疾跑状态（这会在开始疾跑时重置触发标志）
        SprintingStateTracker.updateSprintingState(player, isCurrentlySprinting);

        // 检查从疾跑转为潜行的条件
        if (wasSprintingLastTick  && isCurrentlySneaking && SprintingStateTracker.canTrigger(player)) {
            ShapeShifterCurseFabric.LOGGER.info("Triggering sprint-to-sneak action for player: {}", player.getName().getString());

            // 设置已触发标志
            SprintingStateTracker.setTriggered(player);

            // 发送网络包到服务器
            if (player.level().isClientSide()) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                ClientPlayNetworking.send(new BytePayload(BytePayload.id(ModPackets.SPRINTING_TO_SNEAKING_EVENT_ID), buf));
            }
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void cleanupSprintingState(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        SprintingStateTracker.removePlayer(player);
    }

    @ModifyVariable(method = "makeStuckInBlock", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Vec3 SlowdownPercentMixin(Vec3 multiplier) {
        Player player = (Player) (Object) this;
        List<SlowdownPercentPower> slowdownPower = PowerHolderComponent.getPowers(player, SlowdownPercentPower.class);
        float slowdownPercent = 1.0f;
        for (SlowdownPercentPower power : slowdownPower) {
            slowdownPercent *= power.Multiplier;
        }
        return multiplier.scale(slowdownPercent);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.noMoveTick > 0) {
            this.noMoveTick--;
        }
    }

    @Unique
    public int noMoveTick = 0;

    @Override
    public void shape_shifter_curse$setNoMoveTick(int tick) {
        this.noMoveTick = tick;
    }

    @Override
    public int shape_shifter_curse$getNoMoveTick() {
        return this.noMoveTick;
    }

}