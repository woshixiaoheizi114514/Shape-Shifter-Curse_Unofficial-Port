package net.onixary.shapeShifterCurseFabric.studio;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/** Generic registered accessory; power callbacks are supplied by the existing accessory mixin. */
public final class StudioAccessory extends AccessoryItem {
    private final String description;
    private final Set<ResourceLocation> allowedForms;

    public StudioAccessory(Properties properties, String description, Set<ResourceLocation> allowedForms) {
        super(properties);
        this.description = description;
        this.allowedForms = Set.copyOf(allowedForms);
    }

    @Override
    public boolean canEquip(ItemStack stack, LivingEntity entity, SlotData slot) {
        return allowedForms.isEmpty() || (entity instanceof Player player &&
                allowedForms.contains(PlayerFormComponent.COMPONENT.get(player).nowFormID));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext world, List<Component> tooltip, TooltipFlag context) {
        if (!description.isEmpty()) tooltip.add(Component.literal(description));
    }
}
