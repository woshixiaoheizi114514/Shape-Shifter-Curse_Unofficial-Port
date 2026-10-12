package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
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
    public Text getResourceNotEnoughText(@NotNull ICost costObject, @Nullable PlayerEntity player) {
        return Text.translatable("gui.shape-shifter-curse.perk.insufficient_xp");
    }
}
