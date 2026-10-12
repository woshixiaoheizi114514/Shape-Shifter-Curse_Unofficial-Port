package net.onixary.shapeShifterCurseFabric.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Renders the thrown charm; healing charms apply their potion without arrow impact damage. */
public class PotionCharmArrowEntity extends Arrow implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> CHARM_STACK = SynchedEntityData.defineId(PotionCharmArrowEntity.class, EntityDataSerializers.ITEM_STACK);

    public PotionCharmArrowEntity(EntityType<? extends PotionCharmArrowEntity> type, World world) {
        super(type, world);
        pickup = Pickup.DISALLOWED;
    }
    @Override public void setOwner(Entity owner) {
        super.setOwner(owner);
        // AbstractArrow enables pickup when a player owner is assigned.
        pickup = Pickup.DISALLOWED;
    }
    @Override protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(CHARM_STACK, new ItemStack(Items.PAPER));
    }

    public void setCharmStack(ItemStack stack) {
        dataTracker.set(CHARM_STACK, stack.copyWithCount(1));
    }

    @Override public ItemStack getStack() { return dataTracker.get(CHARM_STACK); }

    @Override protected void onEntityHit(EntityHitResult hit) {
        if (!getStack().isOf(RegCustomItem.HEALING_CHARM)) {
            super.onEntityHit(hit);
            return;
        }
        if (!getWorld().isClient) {
            if (hit.getEntity() instanceof LivingEntity target) {
                onHit(target);
            }
            discard();
        }
    }

    @Override public void writeCustomDataToNbt(CompoundTag nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("CharmStack", getStack().writeNbt(new CompoundTag()));
    }

    @Override public void readCustomDataFromNbt(CompoundTag nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("CharmStack")) {
            setCharmStack(ItemStack.fromNbt(nbt.getCompound("CharmStack")));
        }
    }
}
