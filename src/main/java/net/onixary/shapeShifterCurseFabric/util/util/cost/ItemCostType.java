package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemCostType implements IFUSDrawableCostType<ItemCostType> {
    private static final ResourceLocation id = ShapeShifterCurseFabric.identifier("item");
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
        if (!(costObject instanceof ItemCost cost) || player == null) {
            return IFUSDrawableCostType.super.getAmountText(costObject, player);
        }
        return Component.literal(String.valueOf(cost.getAmount()))
                .append(Component.translatable("gui.shape-shifter-curse.perk.cost_separator"))
                .append(String.valueOf(getAvailableAmount(cost, player)));
    }

    private int getAvailableAmount(ItemCost cost, Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            // Yarn 的 canCombine = 比 item + 组件、不比 count，对应 Mojmap 的 isSameItemSameComponents。
            // 注意别用 ItemStack.matches：它会连 count 一起比，而 exampleStack 恒为 1 个，会永远匹配不上。
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(cost.getExampleStack(), stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    @Override
    public boolean canPay(@NotNull ICost costObject, @Nullable Player player) {
        if (!(costObject instanceof ItemCost cost)) {
            throw new RuntimeException("ItemCostType.canPay costObject must be ItemCost");
        }
        ItemStack exampleStack = cost.getExampleStack();
        int amount = cost.getAmount();
        if (exampleStack.isEmpty() || amount <= 0) {
            return true;
        }
        if (player == null) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                player = ClientUtils.getPlayer();
            } else {
                throw new RuntimeException("CostType.canPay Player Argument In ServerSide Must NotNull");
            }
        }
        return getAvailableAmount(cost, player) >= amount;
    }

    @Override
    public void pay(@NotNull ICost costObject, @NotNull Player player) {
        if (!(costObject instanceof ItemCost cost)) {
            throw new RuntimeException("ItemCostType.pay costObject must be ItemCost");
        }
        ItemStack exampleStack = cost.getExampleStack();
        int amount = cost.getAmount();
        if (exampleStack.isEmpty() || amount <= 0) {
            return;
        }
        Inventory playerInventory = player.getInventory();
        int remaining = amount;
        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            if (remaining <= 0) {
                break;
            }
            ItemStack stack = playerInventory.getItem(i);
            // Yarn 的 canCombine = 比 item + NBT、不比 count，对应 Mojmap 的 isSameItemSameComponents。
            // 注意别用 ItemStack.matches：它会连 count 一起比，而 exampleStack 恒为 1 个，会永远匹配不上。
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(exampleStack, stack)) {
                int take = Math.min(stack.getCount(), remaining);
                stack.shrink(take);
                remaining -= take;
            }
        }
        if (remaining > 0) {
            ShapeShifterCurseFabric.LOGGER.warn("ItemCostType.pay: insufficient items, remaining {}", remaining);
        }
    }
}
