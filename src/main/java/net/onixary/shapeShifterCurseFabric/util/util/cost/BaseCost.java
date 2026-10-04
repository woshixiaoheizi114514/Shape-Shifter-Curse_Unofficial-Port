package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class BaseCost implements ICost {
    public static final ResourceLocation id = ShapeShifterCurseFabric.identifier("base");
    private ICostType<?> type;
    private int amount;

    public BaseCost() {
        this(RegCostType.NO_COST, 0);
    }

    public BaseCost(CompoundTag nbt, HolderLookup.Provider registries) {
        this();
        readFromNBT(nbt, registries);
    }

    public BaseCost(ICostType<?> type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public ICostType<?> getType() {
        return type;
    }


    @Override
    public int getAmount() {
        return amount;
    }


    // BaseCost 只存 type + amount，用不到 registries，但签名必须与 ICost 一致。
    @Override
    public void writeToNBT(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putString("type", type.getID().toString());
        nbt.putInt("amount", amount);
    }


    @Override
    public void readFromNBT(CompoundTag nbt, HolderLookup.Provider registries) {
        try {
            type = RegCostType.getCostType(ResourceLocation.tryParse(nbt.getString("type")));
            amount = nbt.getInt("amount");
        } catch (Exception e) {
            type = RegCostType.NO_COST;
            amount = 0;
        }
    }
}
