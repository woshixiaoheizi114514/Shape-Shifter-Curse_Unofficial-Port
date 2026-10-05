package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyCostType implements IFUSDrawableCostType<EmptyCostType> {
    private static final ResourceLocation id = ShapeShifterCurseFabric.identifier("empty");

    @Override
    public ResourceLocation getID() {
        return id;
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