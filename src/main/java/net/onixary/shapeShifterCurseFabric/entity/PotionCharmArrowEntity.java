package net.onixary.shapeShifterCurseFabric.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import org.jetbrains.annotations.NotNull;

/** Renders the thrown charm; healing charms apply their potion without arrow impact damage. */
public class PotionCharmArrowEntity extends Arrow implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> CHARM_STACK = SynchedEntityData.defineId(PotionCharmArrowEntity.class, EntityDataSerializers.ITEM_STACK);

    public PotionCharmArrowEntity(EntityType<? extends PotionCharmArrowEntity> type, Level world) {
        super(type, world);
        pickup = Pickup.DISALLOWED;
    }
    @Override public void setOwner(Entity owner) {
        super.setOwner(owner);
        // AbstractArrow enables pickup when a player owner is assigned.
        pickup = Pickup.DISALLOWED;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // Yarn DataTracker.startTracking → Mojmap 在 Builder 上 define
        builder.define(CHARM_STACK, new ItemStack(Items.PAPER));
    }

    public void setCharmStack(ItemStack stack) {
        entityData.set(CHARM_STACK, stack.copyWithCount(1));
    }

    @Override public @NotNull ItemStack getItem() { return entityData.get(CHARM_STACK); }

    @Override protected void onHitEntity(EntityHitResult hit) {
        if (!getItem().is(RegCustomItem.HEALING_CHARM)) {
            super.onHitEntity(hit);
            return;
        }
        if (!level().isClientSide) {
            if (hit.getEntity() instanceof LivingEntity target) {
                // Yarn PersistentProjectileEntity.onHit(LivingEntity) → Mojmap 的对应钩子是 doPostHurtEffects
                // （Arrow 已重写它来施加药水效果，且不造成箭矢伤害 —— 正是这里要的语义）
                doPostHurtEffects(target);
            }
            discard();
        }
    }

    /**
     * 上游 Yarn 侧 {@code PersistentProjectileEntity.initFromStack(ItemStack)} 是**父类的 public 方法**，
     * 所以 PotionCharmItem 能直接调。Mojmap 的等价物是 {@code AbstractArrow.setPickupItemStack(ItemStack)}，
     * 却是 <b>protected</b>，外部够不着 —— 故在此转发一次。
     * 传入的栈会成为 pickupItemStack，{@code Arrow.doPostHurtEffects} 正是从它读药水内容并施加给命中目标。
     *
     * <p>⚠ 上游本次重写本类时（改存 CHARM_STACK）把这段转发覆盖掉了，合并后必须补回来，
     * 否则药水效果完全不生效（箭还是照飞，只是命中不给效果）。</p>
     */
    public void initFromStack(ItemStack stack) {
        this.setPickupItemStack(stack);
    }

    @Override public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.put("CharmStack", getItem().save(level().registryAccess()));
    }

    @Override public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("CharmStack")) {
            setCharmStack(ItemStack.parseOptional(level().registryAccess(), nbt.getCompound("CharmStack")));
        }
    }
}
