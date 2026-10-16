package net.onixary.shapeShifterCurseFabric.magic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RegMagic {
    public static HashMap<ResourceLocation, IMagic> magicRegistry = new HashMap<>();

    public static IMagic ACTIVE_SKILL_1 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_1"), RegPerks.PerkSpriteMap.bi(1, 0), "key.shape-shifter-curse.active_skill_1"));
    public static IMagic ACTIVE_SKILL_2 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_2"), RegPerks.PerkSpriteMap.bi(2, 0), "key.shape-shifter-curse.active_skill_2"));
    public static IMagic ACTIVE_SKILL_3 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_3"), RegPerks.PerkSpriteMap.bi(3, 0), "key.shape-shifter-curse.active_skill_3"));
    public static IMagic ACTIVE_SKILL_4 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_4"), RegPerks.PerkSpriteMap.bi(4, 0), "key.shape-shifter-curse.active_skill_4"));
    public static IMagic ACTIVE_SKILL_5 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_5"), RegPerks.PerkSpriteMap.bi(5, 0), "key.shape-shifter-curse.active_skill_5"));
    public static IMagic ACTIVE_SKILL_6 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_6"), RegPerks.PerkSpriteMap.bi(6, 0), "key.shape-shifter-curse.active_skill_6"));

    public static IMagic registerMagic(IMagic magic) {
        ResourceLocation id = magic.getId();
        magicRegistry.put(id, magic);
        return magic;
    }

    public static @Nullable IMagic getMagic(ResourceLocation id) {
        return magicRegistry.get(id);
    }
}
