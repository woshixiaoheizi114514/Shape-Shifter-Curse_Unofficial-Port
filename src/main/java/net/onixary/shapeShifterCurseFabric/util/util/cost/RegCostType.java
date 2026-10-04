package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.BiFunction;

public class RegCostType {
    public static final HashMap<ResourceLocation, ICostType<?>> costTypes = new HashMap<>();
    public static final HashMap<ResourceLocation, BiFunction<CompoundTag, HolderLookup.Provider, ICost>> costs = new HashMap<>();

    public static final EmptyCostType NO_COST = register(new EmptyCostType());
    public static final XPCostType COST_XP = register(new XPCostType());
    public static final ItemCostType COST_ITEM = register(new ItemCostType());

    static {
        costs.put(BaseCost.id, BaseCost::new);
        costs.put(ItemCost.id, ItemCost::new);
    }

    public static <T extends ICostType<T>> T register(@NotNull T costType) {
        ResourceLocation id = costType.getID();
        costTypes.put(id, costType);
        return costType;
    }

    public static @Nullable ICostType<?> getCostType(ResourceLocation id) {
        return costTypes.get(id);
    }
}
