package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.custom_ui.FormUpgradeScreen;
import net.onixary.shapeShifterCurseFabric.util.util.BaseSprite;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyCostType implements IFUSDrawableCostType<EmptyCostType> {
    private static final ResourceLocation id = ShapeShifterCurseFabric.identifier("empty");
    private static final ISprite xpIconSprite = new BaseSprite(FormUpgradeScreen.TEXTURE, FormUpgradeScreen.TEXTURE_WIDTH, FormUpgradeScreen.TEXTURE_HEIGHT, 434, 35, 18, 18);

    @Override
    public ResourceLocation getID() {
        return id;
    }

    @Override
    public void drawIcon(GuiGraphics context, @NotNull ICost costObject, @Nullable Player player, int x, int y, int z) {
        xpIconSprite.draw(context, x, y, z, 0, 0, 18, 18);
    }

    @Override
    public void drawOnHover(GuiGraphics context, @NotNull ICost costObject, @Nullable Player player, int x, int y, int z, int mouseX, int mouseY) {
        // NOP
    }

    @Override
    public Component getAmountText(@NotNull ICost costObject, @Nullable Player player) {
        return Component.literal("");
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