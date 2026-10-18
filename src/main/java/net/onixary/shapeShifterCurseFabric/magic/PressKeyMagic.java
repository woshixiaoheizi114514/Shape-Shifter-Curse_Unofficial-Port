package net.onixary.shapeShifterCurseFabric.magic;

import net.minecraft.resources.ResourceLocation;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;

public class PressKeyMagic implements IMagic {
    public final ResourceLocation id;
    public final ISprite sprite;
    public final ISprite unselectedSprite;
    public final String KeyBindID;

    public PressKeyMagic(ResourceLocation id, ISprite sprite, String KeyBindID) {
        this(id, sprite, sprite, KeyBindID);
    }

    public PressKeyMagic(ResourceLocation id, ISprite sprite, ISprite unselectedSprite, String KeyBindID) {
        this.id = id;
        this.sprite = sprite;
        this.unselectedSprite = unselectedSprite;
        this.KeyBindID = KeyBindID;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public ISprite getSprite() {
        return sprite;
    }

    @Override
    public ISprite getUnselectedSprite() {
        return unselectedSprite;
    }

    public String getKeyBindID() {
        return this.KeyBindID;
    }

    @Override
    public boolean virtualInvoke() {
        return true;
    }
}
