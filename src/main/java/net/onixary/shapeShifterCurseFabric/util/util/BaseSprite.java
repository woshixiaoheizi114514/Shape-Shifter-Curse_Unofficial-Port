package net.onixary.shapeShifterCurseFabric.util.util;

import net.minecraft.resources.ResourceLocation;

public class BaseSprite implements ISprite {
    public final ResourceLocation textureID;
    public final int textureImgWidth;
    public final int textureImgHeight;
    public final int textureX;
    public final int textureY;
    public final int textureWidth;
    public final int textureHeight;
    public BaseSprite(ResourceLocation textureID, int textureImgWidth, int textureImgHeight, int textureX, int textureY, int textureWidth, int textureHeight) {
        this.textureID = textureID;
        this.textureImgWidth = textureImgWidth;
        this.textureImgHeight = textureImgHeight;
        this.textureX = textureX;
        this.textureY = textureY;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public ResourceLocation getTextureID() {
        return textureID;
    }

    @Override
    public int getTextureImgWidth() {
        return textureImgWidth;
    }

    @Override
    public int getTextureImgHeight() {
        return textureImgHeight;
    }

    @Override
    public int getTextureX() {
        return textureX;
    }

    @Override
    public int getTextureY() {
        return textureY;
    }

    @Override
    public int getTextureWidth() {
        return textureWidth;
    }

    @Override
    public int getTextureHeight() {
        return textureHeight;
    }
}
