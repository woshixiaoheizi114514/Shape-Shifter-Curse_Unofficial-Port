package net.onixary.shapeShifterCurseFabric.mixin.forge;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.util.Accessory.CurioUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(CurioUtils.class)
public class CurioUtilsImpl {

    @Overwrite(remap = false)
    public static boolean isLoaded() {
        return true;
    }

    @Overwrite(remap = false)
    public static boolean isEquipped(LivingEntity entity, Item item) {
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return false;
        }
        for (ICurioStacksHandler stacksHandler : handler.getCurios().values()) {
            IDynamicStackHandler stacks = stacksHandler.getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                if (stacks.getStackInSlot(i).is(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Overwrite(remap = false)
    public static Map<String, List<ItemStack>> getEntitySlots(LivingEntity entity) {
        Map<String, List<ItemStack>> map = new HashMap<>();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return map;
        }
        for (Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
            IDynamicStackHandler stacks = entry.getValue().getStacks();
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < stacks.getSlots(); i++) {
                list.add(stacks.getStackInSlot(i));
            }
            map.put(entry.getKey(), list);
        }
        return map;
    }

    @Overwrite(remap = false)
    public static List<ItemStack> getEntitySlot(LivingEntity entity, String SlotName) {
        List<ItemStack> list = new ArrayList<>();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return list;
        }
        ICurioStacksHandler stacksHandler = handler.getCurios().get(SlotName);
        if (stacksHandler != null) {
            IDynamicStackHandler stacks = stacksHandler.getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                list.add(stacks.getStackInSlot(i));
            }
        }
        return list;
    }

    @Overwrite(remap = false)
    public static void setEntitySlot(LivingEntity entity, String SlotName, int Index, ItemStack stack) {
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return;
        }
        ICurioStacksHandler stacksHandler = handler.getCurios().get(SlotName);
        if (stacksHandler != null && Index >= 0 && Index < stacksHandler.getStacks().getSlots()) {
            stacksHandler.getStacks().setStackInSlot(Index, stack);
        }
    }

    @Overwrite(remap = false)
    public static Map<String, List<ItemStack>> getEntityCosmeticSlots(LivingEntity entity) {
        Map<String, List<ItemStack>> map = new HashMap<>();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return map;
        }
        for (Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
            IDynamicStackHandler stacks = entry.getValue().getCosmeticStacks();
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < stacks.getSlots(); i++) {
                list.add(stacks.getStackInSlot(i));
            }
            map.put(entry.getKey(), list);
        }
        return map;
    }

    @Overwrite(remap = false)
    public static List<ItemStack> getEntityCosmeticSlot(LivingEntity entity, String SlotName) {
        List<ItemStack> list = new ArrayList<>();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return list;
        }
        ICurioStacksHandler stacksHandler = handler.getCurios().get(SlotName);
        if (stacksHandler != null) {
            IDynamicStackHandler stacks = stacksHandler.getCosmeticStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                list.add(stacks.getStackInSlot(i));
            }
        }
        return list;
    }

    @Overwrite(remap = false)
    public static void setEntityCosmeticSlot(LivingEntity entity, String SlotName, int Index, ItemStack stack) {
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return;
        }
        ICurioStacksHandler stacksHandler = handler.getCurios().get(SlotName);
        if (stacksHandler != null && Index >= 0 && Index < stacksHandler.getCosmeticStacks().getSlots()) {
            stacksHandler.getCosmeticStacks().setStackInSlot(Index, stack);
        }
    }

    @Overwrite(remap = false)
    public static Map<String, List<Boolean>> getEntitySlotRenders(LivingEntity entity) {
        Map<String, List<Boolean>> map = new HashMap<>();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return map;
        }
        for (Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
            map.put(entry.getKey(), new ArrayList<>(entry.getValue().getRenders()));
        }
        return map;
    }

    @Overwrite(remap = false)
    public static List<Boolean> getEntitySlotRender(LivingEntity entity, String SlotName) {
        List<Boolean> list = new ArrayList<>();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return list;
        }
        ICurioStacksHandler stacksHandler = handler.getCurios().get(SlotName);
        if (stacksHandler != null) {
            list.addAll(stacksHandler.getRenders());
        }
        return list;
    }

    @Overwrite(remap = false)
    public static void setEntitySlotRender(LivingEntity entity, String SlotName, int Index, boolean render) {
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) {
            return;
        }
        ICurioStacksHandler stacksHandler = handler.getCurios().get(SlotName);
        if (stacksHandler != null) {
            NonNullList<Boolean> renders = stacksHandler.getRenders();
            if (Index >= 0 && Index < renders.size()) {
                renders.set(Index, render);
            }
        }
    }
}
