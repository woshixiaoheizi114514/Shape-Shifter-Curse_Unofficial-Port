package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XPCostType implements IFUSDrawableCostType<XPCostType> {
    private static final ResourceLocation id = ShapeShifterCurseFabric.identifier("xp");
    public static final ResourceLocation TEXTURE = ShapeShifterCurseFabric.identifier("textures/gui/shape_shifter_tuner_ui.png");
    public static final int TEXTURE_WIDTH = 454;
    public static final int TEXTURE_HEIGHT = 190;

    // ⚠ 图标绘制（原 drawIcon / drawOnHover）刻意**不放在这里**：它们签名含客户端类 GuiGraphics，
    //   而本类会在专用服务端被 RegCostType 的静态初始化注册，链接期加载客户端类会让服务端启动崩溃
    //   （Cannot load class ... Screen in environment type SERVER）。绘制已整体移到 CostTypeIcons。
    //   上游本次把这两个方法加了回来，合并时按本仓库既有架构剔除（逻辑与 CostTypeIcons 完全一致）。


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
        if (player == null) {
            return IFUSDrawableCostType.super.getAmountText(costObject, player);
        }
        return Component.literal(String.valueOf(costObject.getAmount()))
                .append(Component.translatable("gui.shape-shifter-curse.perk.cost_separator"))
                .append(String.valueOf(player.totalExperience));
    }

    @Override
    public boolean canPay(@NotNull ICost costObject, @Nullable Player player) {
        int costAmount = costObject.getAmount();
        if (player == null) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                player = ClientUtils.getPlayer();
            } else {
                throw new RuntimeException("CostType.canPay Player Argument In ServerSide Must NotNull");
            }
        }
        return player.totalExperience >= costAmount;
    }

    @Override
    public void pay(@NotNull ICost costObject, @NotNull Player player) {
        player.giveExperiencePoints(-costObject.getAmount());
    }

    @Override
    public Component getResourceNotEnoughText(@NotNull ICost costObject, @Nullable Player player) {
        return Component.translatable("gui.shape-shifter-curse.perk.insufficient_xp");
    }
}
