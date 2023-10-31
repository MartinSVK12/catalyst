package sunsetsatellite.catalyst.core.util;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TextureFX;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Color;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;

public class RenderBlockSimple {
    public int overrideBlockTexture = -1;
    public boolean flipTexture = false;
    private int uvRotateEast = 0;
    private int uvRotateWest = 0;
    private int uvRotateSouth = 0;
    private int uvRotateNorth = 0;
    private int uvRotateTop = 0;
    private int uvRotateBottom = 0;
    public boolean enableAO = false;
    public float colorRedTopLeft = 0.0F;
    public float colorRedBottomLeft = 0.0F;
    public float colorRedBottomRight = 0.0F;
    public float colorRedTopRight = 0.0F;
    public float colorGreenTopLeft = 0.0F;
    public float colorGreenBottomLeft = 0.0F;
    public float colorGreenBottomRight = 0.0F;
    public float colorGreenTopRight = 0.0F;
    public float colorBlueTopLeft = 0.0F;
    public float colorBlueBottomLeft = 0.0F;
    public float colorBlueBottomRight = 0.0F;
    public float colorBlueTopRight = 0.0F;

    public RenderBlockSimple() {
        this.uvRotateEast = 0;
        this.uvRotateWest = 0;
        this.uvRotateSouth = 0;
        this.uvRotateNorth = 0;
        this.uvRotateTop = 0;
        this.uvRotateBottom = 0;
    }

    public void renderBottomFace(Block block, double d, double d1, double d2, int i) {
        Tessellator tessellator = Tessellator.instance;
        if (this.overrideBlockTexture >= 0) {
            i = this.overrideBlockTexture;
        }

        int j = i % Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        int k = i / Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        double d3 = ((double)j + block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d4 = ((double)j + block.maxX * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d5 = ((double)k + block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d6 = ((double)k + block.maxZ * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        if (block.minX < 0.0 || block.maxX > 1.0) {
            d3 = ((float)j + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((float)j + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        if (block.minZ < 0.0 || block.maxZ > 1.0) {
            d5 = ((float)k + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((float)k + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;
        if (this.uvRotateBottom == 2) {
            d3 = ((double)j + block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)j + block.maxZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        } else if (this.uvRotateBottom == 1) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.maxX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        } else if (this.uvRotateBottom == 3) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxX * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxZ * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = d + block.minX;
        double d12 = d + block.maxX;
        double d13 = d1 + block.minY;
        double d14 = d2 + block.minZ;
        double d15 = d2 + block.maxZ;
        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        } else {
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        }

    }

    public void renderTopFace(Block block, double d, double d1, double d2, int i) {
        Tessellator tessellator = Tessellator.instance;
        if (this.overrideBlockTexture >= 0) {
            i = this.overrideBlockTexture;
        }

        int j = i % Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        int k = i / Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        double d3 = ((double)j + block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d4 = ((double)j + block.maxX * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d5 = ((double)k + block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d6 = ((double)k + block.maxZ * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        if (block.minX < 0.0 || block.maxX > 1.0) {
            d3 = ((float)j + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((float)j + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        if (block.minZ < 0.0 || block.maxZ > 1.0) {
            d5 = ((float)k + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((float)k + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;
        if (this.uvRotateTop == 1) {
            d3 = ((double)j + block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)j + block.maxZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        } else if (this.uvRotateTop == 2) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.maxX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        } else if (this.uvRotateTop == 3) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxX * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxZ * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        double d11 = d + block.minX;
        double d12 = d + block.maxX;
        double d13 = d1 + block.maxY;
        double d14 = d2 + block.minZ;
        double d15 = d2 + block.maxZ;
        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        } else {
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        }

    }

    public void renderEastFace(Block block, double d, double d1, double d2, int i) {
        Tessellator tessellator = Tessellator.instance;
        if (this.overrideBlockTexture >= 0) {
            i = this.overrideBlockTexture;
        }

        int j = i % Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        int k = i / Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        double d3 = ((double)j + block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d4 = ((double)j + block.maxX * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.minY * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d8;
        if (this.flipTexture) {
            d8 = d3;
            d3 = d4;
            d4 = d8;
        }

        if (block.minX < 0.0 || block.maxX > 1.0) {
            d3 = ((float)j + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((float)j + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        if (block.minY < 0.0 || block.maxY > 1.0) {
            d5 = ((float)k + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((float)k + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        d8 = d4;
        double d9 = d3;
        double d10 = d5;
        double d11 = d6;
        if (this.uvRotateEast == 2) {
            d3 = ((double)j + block.minY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)j + block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d10 = d5;
            d11 = d6;
            d8 = d3;
            d9 = d4;
            d5 = d6;
            d6 = d10;
        } else if (this.uvRotateEast == 1) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.maxX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.minY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d8 = d4;
            d9 = d3;
            d3 = d4;
            d4 = d9;
            d10 = d6;
            d11 = d5;
        } else if (this.uvRotateEast == 3) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxX * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.minY * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d8 = d4;
            d9 = d3;
            d10 = d5;
            d11 = d6;
        }

        double d12 = d + block.minX;
        double d13 = d + block.maxX;
        double d14 = d1 + block.minY;
        double d15 = d1 + block.maxY;
        double d16 = d2 + block.minZ;
        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.addVertexWithUV(d12, d15, d16, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.addVertexWithUV(d13, d15, d16, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.addVertexWithUV(d13, d14, d16, d9, d11);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.addVertexWithUV(d12, d14, d16, d4, d6);
        } else {
            tessellator.addVertexWithUV(d12, d15, d16, d8, d10);
            tessellator.addVertexWithUV(d13, d15, d16, d3, d5);
            tessellator.addVertexWithUV(d13, d14, d16, d9, d11);
            tessellator.addVertexWithUV(d12, d14, d16, d4, d6);
        }

    }

    public void renderWestFace(Block block, double d, double d1, double d2, int i) {
        Tessellator tessellator = Tessellator.instance;
        if (this.overrideBlockTexture >= 0) {
            i = this.overrideBlockTexture;
        }

        int j = i % Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        int k = i / Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        double d3 = ((double)j + block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d4 = ((double)j + block.maxX * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.minY * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d8;
        if (this.flipTexture) {
            d8 = d3;
            d3 = d4;
            d4 = d8;
        }

        if (block.minX < 0.0 || block.maxX > 1.0) {
            d3 = ((float)j + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((float)j + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        if (block.minY < 0.0 || block.maxY > 1.0) {
            d5 = ((float)k + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((float)k + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        d8 = d4;
        double d9 = d3;
        double d10 = d5;
        double d11 = d6;
        if (this.uvRotateWest == 1) {
            d3 = ((double)j + block.minY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)j + block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d10 = d5;
            d11 = d6;
            d8 = d3;
            d9 = d4;
            d5 = d6;
            d6 = d10;
        } else if (this.uvRotateWest == 2) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.minY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.maxX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d8 = d4;
            d9 = d3;
            d3 = d4;
            d4 = d9;
            d10 = d6;
            d11 = d5;
        } else if (this.uvRotateWest == 3) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.minX * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxX * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.minY * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d8 = d4;
            d9 = d3;
            d10 = d5;
            d11 = d6;
        }

        double d12 = d + block.minX;
        double d13 = d + block.maxX;
        double d14 = d1 + block.minY;
        double d15 = d1 + block.maxY;
        double d16 = d2 + block.maxZ;
        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.addVertexWithUV(d12, d15, d16, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.addVertexWithUV(d12, d14, d16, d9, d11);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.addVertexWithUV(d13, d14, d16, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.addVertexWithUV(d13, d15, d16, d8, d10);
        } else {
            tessellator.addVertexWithUV(d12, d15, d16, d3, d5);
            tessellator.addVertexWithUV(d12, d14, d16, d9, d11);
            tessellator.addVertexWithUV(d13, d14, d16, d4, d6);
            tessellator.addVertexWithUV(d13, d15, d16, d8, d10);
        }

    }

    public void renderNorthFace(Block block, double d, double d1, double d2, int i) {
        Tessellator tessellator = Tessellator.instance;
        if (this.overrideBlockTexture >= 0) {
            i = this.overrideBlockTexture;
        }

        int j = i % Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        int k = i / Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        double d3 = ((double)j + block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d4 = ((double)j + block.maxZ * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.minY * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d8;
        if (this.flipTexture) {
            d8 = d3;
            d3 = d4;
            d4 = d8;
        }

        if (block.minZ < 0.0 || block.maxZ > 1.0) {
            d3 = ((float)j + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((float)j + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        if (block.minY < 0.0 || block.maxY > 1.0) {
            d5 = ((float)k + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((float)k + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        d8 = d4;
        double d9 = d3;
        double d10 = d5;
        double d11 = d6;
        if (this.uvRotateNorth == 1) {
            d3 = ((double)j + block.minY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)j + block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d10 = d5;
            d11 = d6;
            d8 = d3;
            d9 = d4;
            d5 = d6;
            d6 = d10;
        } else if (this.uvRotateNorth == 2) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.minY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.maxZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d8 = d4;
            d9 = d3;
            d3 = d4;
            d4 = d9;
            d10 = d6;
            d11 = d5;
        } else if (this.uvRotateNorth == 3) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxZ * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.minY * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d8 = d4;
            d9 = d3;
            d10 = d5;
            d11 = d6;
        }

        double d12 = d + block.minX;
        double d13 = d1 + block.minY;
        double d14 = d1 + block.maxY;
        double d15 = d2 + block.minZ;
        double d16 = d2 + block.maxZ;
        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.addVertexWithUV(d12, d14, d16, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.addVertexWithUV(d12, d13, d15, d9, d11);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.addVertexWithUV(d12, d13, d16, d4, d6);
        } else {
            tessellator.addVertexWithUV(d12, d14, d16, d8, d10);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d15, d9, d11);
            tessellator.addVertexWithUV(d12, d13, d16, d4, d6);
        }

    }

    public void renderSouthFace(Block block, double d, double d1, double d2, int i) {
        Tessellator tessellator = Tessellator.instance;
        if (this.overrideBlockTexture >= 0) {
            i = this.overrideBlockTexture;
        }

        int j = i % Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        int k = i / Global.TEXTURE_ATLAS_WIDTH_TILES * TextureFX.tileWidthTerrain;
        double d3 = ((double)j + block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d4 = ((double)j + block.maxZ * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.minY * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        double d8;
        if (this.flipTexture) {
            d8 = d3;
            d3 = d4;
            d4 = d8;
        }

        if (block.minZ < 0.0 || block.maxZ > 1.0) {
            d3 = ((float)j + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((float)j + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        if (block.minY < 0.0 || block.maxY > 1.0) {
            d5 = ((float)k + 0.0F) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((float)k + ((float)TextureFX.tileWidthTerrain - 0.01F)) / (float)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
        }

        d8 = d4;
        double d9 = d3;
        double d10 = d5;
        double d11 = d6;
        if (this.uvRotateSouth == 2) {
            d3 = ((double)j + block.minY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)(k + TextureFX.tileWidthTerrain) - block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)j + block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)(k + TextureFX.tileWidthTerrain) - block.maxZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d10 = d5;
            d11 = d6;
            d8 = d3;
            d9 = d4;
            d5 = d6;
            d6 = d10;
        } else if (this.uvRotateSouth == 1) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.maxZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.minY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d8 = d4;
            d9 = d3;
            d3 = d4;
            d4 = d9;
            d10 = d6;
            d11 = d5;
        } else if (this.uvRotateSouth == 3) {
            d3 = ((double)(j + TextureFX.tileWidthTerrain) - block.minZ * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d4 = ((double)(j + TextureFX.tileWidthTerrain) - block.maxZ * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d5 = ((double)k + block.maxY * (double)TextureFX.tileWidthTerrain) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d6 = ((double)k + block.minY * (double)TextureFX.tileWidthTerrain - 0.01) / (double)(TextureFX.tileWidthTerrain * Global.TEXTURE_ATLAS_WIDTH_TILES);
            d8 = d4;
            d9 = d3;
            d10 = d5;
            d11 = d6;
        }

        double d12 = d + block.maxX;
        double d13 = d1 + block.minY;
        double d14 = d1 + block.maxY;
        double d15 = d2 + block.minZ;
        double d16 = d2 + block.maxZ;
        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.addVertexWithUV(d12, d13, d16, d9, d11);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.addVertexWithUV(d12, d14, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.addVertexWithUV(d12, d14, d16, d3, d5);
        } else {
            tessellator.addVertexWithUV(d12, d13, d16, d9, d11);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d14, d15, d8, d10);
            tessellator.addVertexWithUV(d12, d14, d16, d3, d5);
        }

    }

    public void renderBlock(Block block, int i, World world, int x, int y, int z) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        if (block.id == Block.fluidWaterFlowing.id) {
            Color c = (new Color()).setARGB(BlockColorDispatcher.getInstance().getDispatch(Block.fluidWaterFlowing).getWorldColor(world, x, y, z));//Block.fluidWaterFlowing.colorMultiplier(world, world, x, y, z));
            GL11.glColor4f((float)c.getRed() / 255.0F, (float)c.getGreen() / 255.0F, (float)c.getBlue() / 255.0F, 1.0F);
        }

        this.renderBottomFace(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSideAndMetadata(Side.BOTTOM, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        this.renderTopFace(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSideAndMetadata(Side.TOP, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        this.renderEastFace(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSideAndMetadata(Side.NORTH, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        this.renderWestFace(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSideAndMetadata(Side.SOUTH, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        this.renderNorthFace(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSideAndMetadata(Side.WEST, i));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        this.renderSouthFace(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSideAndMetadata(Side.EAST, i));
        tessellator.draw();
    }
}
