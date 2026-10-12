package net.onixary.shapeShifterCurseFabric.util.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class BaseSpriteWithDrawOffset extends BaseSprite {
    public final int drawOffsetX;
    public final int drawOffsetY;

    public BaseSpriteWithDrawOffset(ResourceLocation textureID, int textureImgWidth, int textureImgHeight, int drawOffsetX, int drawOffsetY, int textureX, int textureY, int textureWidth, int textureHeight) {
        super(textureID, textureImgWidth, textureImgHeight, textureX, textureY, textureWidth, textureHeight);
        this.drawOffsetX = drawOffsetX;
        this.drawOffsetY = drawOffsetY;
    }

    @Override
    public void draw(GuiGraphics context, int x, int y, int z, int u, int v, int width, int height) {
        context.blit(getTextureID(), x + drawOffsetX, y + drawOffsetY, z, getTextureX() + u, getTextureY() + v, width, height, getTextureImgWidth(), getTextureImgHeight());
    }
}
