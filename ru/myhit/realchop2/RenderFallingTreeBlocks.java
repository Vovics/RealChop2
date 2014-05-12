package ru.myhit.realchop2;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class RenderFallingTreeBlocks extends RenderBlocks{

    public RenderFallingTreeBlocks()
    {
        super();
    }

    public void renderFallingTreeBlock(Block par1Block, World par2World, int parPosX, int parPosY, int parPosZ, int parMetadata) {
    	float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(par2World, parPosX, parPosY, parPosZ));

        int color=par1Block.getRenderColor(parMetadata);
        float c1 = (float)(color >> 16 & 255) / 255.0F;
        float c2 = (float)(color >> 8 & 255) / 255.0F;
        float c3 = (float)(color & 255) / 255.0F;

        tessellator.setColorOpaque_F(c1 * f, c2 * f, c3 * f);
        this.renderFaceYNeg(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 0, parMetadata));

        tessellator.setColorOpaque_F(c1 * f1, c2 * f1, c3 * f1);
        this.renderFaceYPos(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 1, parMetadata));

        tessellator.setColorOpaque_F(c1 * f2, c2 * f2, c3 * f2);
        this.renderFaceZNeg(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 2, parMetadata));

        tessellator.setColorOpaque_F(c1 * f2, c2 * f2, c3 * f2);
        this.renderFaceZPos(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 3, parMetadata));

        tessellator.setColorOpaque_F(c1 * f3, c2 * f3, c3 * f3);
        this.renderFaceXNeg(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 4, parMetadata));

        tessellator.setColorOpaque_F(c1 * f3, c2 * f3, c3 * f3);
        this.renderFaceXPos(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 5, parMetadata));
        tessellator.draw();
    	
    }
}
