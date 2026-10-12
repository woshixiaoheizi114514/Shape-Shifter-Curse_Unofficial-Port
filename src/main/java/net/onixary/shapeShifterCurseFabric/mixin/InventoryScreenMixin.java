package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Unique
    private static float prevBodyYaw;

    // ⚠ 原版 1.21.1 的形参表是：
    //   (GuiGraphics, int x1, int y1, int x2, int y2, int size, float yOffset, float mouseX, float mouseY, LivingEntity)
    //   注意后 5 个 int 是**矩形四边 + 尺寸**，不是 (x, y, size, mouseX, mouseY)；
    //   鼠标是第 7、8 个形参（两个 float）。本类原先按错误的名字取值，
    //   于是把「矩形底边 y2」当成鼠标算角度，写出的 yBodyRotO 与 vanilla 随后写入的 yBodyRot 不一致 ——
    //   后果是预览期间尾巴被持续拖拽（DefaultModelAnimationSystem 的 tailDragAmount 用 yBodyRot-yBodyRotO）、
    //   长颈角度偏移，且**原版背包**与**形态之书**两处预览都会中招。
    @Inject(method = "renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"))
    private static void drawEntityHead(GuiGraphics context, int x1, int y1, int x2, int y2, int size,
                                       float yOffset, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ClientUtils.isOpenInventoryScreen = true;
        prevBodyYaw = entity.yBodyRotO;
        // 按 vanilla 的算法复算 p（见 InventoryScreen#renderEntityInInventoryFollowsMouse 内的
        // p = atan(((x1+x2)/2 - mouseX)/40)、yBodyRot = 180 + p*20），
        // 使预览期间 yBodyRotO 恒等于 yBodyRot —— 这正是本 mixin 想要的效果。
        float p = (float)Math.atan((((x1 + x2) / 2.0F) - mouseX) / 40.0F);
        entity.yBodyRotO = 180.0F + p * 20.0F;
    }

    @Inject(method = "renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V", at = @At("RETURN"))
    private static void drawEntityTail(GuiGraphics context, int x1, int y1, int x2, int y2, int size,
                                       float yOffset, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ClientUtils.isOpenInventoryScreen = false;
        entity.yBodyRotO = prevBodyYaw;
    }
}
