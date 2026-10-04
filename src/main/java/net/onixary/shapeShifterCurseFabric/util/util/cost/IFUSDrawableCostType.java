package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IFUSDrawableCostType<T extends ICostType<T>> extends ICostType<T> {
    public void drawIcon(GuiGraphics context, @NotNull ICost costObject, @Nullable Player player, int x, int y, int z);

    public void drawOnHover(GuiGraphics context, @NotNull ICost costObject, @Nullable Player player, int x, int y, int z, int mouseX, int mouseY);

    public default Component getAmountText(@NotNull ICost costObject, @Nullable Player player) {
        return Component.nullToEmpty(String.valueOf(costObject.getAmount()));
    }
}
