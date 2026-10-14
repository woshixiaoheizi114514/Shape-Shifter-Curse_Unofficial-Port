package net.onixary.shapeShifterCurseFabric.player_animator;

import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.math.Vec3f;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface PlayerAnimatorBridge {
    Object createLayer(AbstractClientPlayer player);

    boolean play(Object container, ResourceLocation animationID, float speed, int fade, boolean needsCleanup);

    boolean playImmediate(Object container, ResourceLocation animationID, boolean needsCleanup);

    void stop(Object container);

    Vec3f getBoneTransform(Player player, String rawBoneName, TransformType type, Vec3f defaultValue);

    boolean isFirstPersonPass();

    @Nullable FirstPersonArms getFirstPersonArms(Player player);
}
