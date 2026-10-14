package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAnimFSM {
    public Tuple<@Nullable ResourceLocation, @NotNull ResourceLocation> update(Player player, AnimSystem.AnimSystemData animSystemData) {
        ResourceLocation NextFSMID = getNextFSM(player, animSystemData);
        if (NextFSMID == null) {
            return new Tuple<>(null, getStateID(player, animSystemData));
        } else {
            AbstractAnimFSM NextFSM = AnimRegistry.getAnimFSM(NextFSMID);
            if (NextFSM == null) {
                ShapeShifterCurseFabric.LOGGER.error("找不到动画控制状态机: {}", NextFSMID);
                return new Tuple<>(null, getStateID(player, animSystemData));
            }
            Tuple<@Nullable ResourceLocation, @NotNull ResourceLocation> NextFSMResult = NextFSM.update(player, animSystemData);
            if (NextFSMResult.getA() != null) {
                return NextFSMResult;
            }
            else {  // 单次跳转
                return new Tuple<>(NextFSMID, NextFSMResult.getB());
            }
        }
    }

	public abstract @Nullable ResourceLocation getNextFSM(Player player, AnimSystem.AnimSystemData animSystemData);

    public abstract @NotNull ResourceLocation getStateID(Player player, AnimSystem.AnimSystemData animSystemData);
}
