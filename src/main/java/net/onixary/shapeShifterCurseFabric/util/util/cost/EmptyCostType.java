package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyCostType implements IFUSDrawableCostType<EmptyCostType> {
    private static final ResourceLocation id = ShapeShifterCurseFabric.identifier("empty");
    public static final ResourceLocation TEXTURE = ShapeShifterCurseFabric.identifier("textures/gui/shape_shifter_tuner_ui.png");
    public static final int TEXTURE_WIDTH = 454;
    public static final int TEXTURE_HEIGHT = 190;

    @Override
    public int checkInterval() {
        return -1;
    }


    @Override
    public void drawIcon(GuiGraphics context, @NotNull ICost costObject, @Nullable Player player, int x, int y, int z) {
        // ⚠ 真正的绘制（含贴图）在 CostTypeIcons（@Environment(CLIENT)），此处只做转发。
        //   本类会在**专用服务端**被 RegCostType.<clinit> 加载，所以这里绝不能出现
        //   「引用客户端类的静态字段/静态初始化」—— 那正是 3afa9362 修的启动崩溃
        //   （new BaseSprite(FormUpgradeScreen.TEXTURE, …) 会让 <clinit> 去加载 Screen）。
        //   方法体内的引用与形参类型都是惰性解析的，服务端不调用本方法就不会加载客户端类。
        CostTypeIcons.drawIcon(this, context, costObject, player, x, y, z);
    }

    @Override
    public ResourceLocation getID() {
        return id;
    }

    @Override
    public Component getAmountText(@NotNull ICost costObject, @Nullable Player player) {
        return Component.empty();
    }

    @Override
    public boolean canPay(@NotNull ICost costObject, @Nullable Player player) {
        return true;
    }

    @Override
    public void pay(@NotNull ICost costObject, @NotNull Player player) {
        return;
    }
}