package ru.myhit.realchop2;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFallingTree extends Render {
	private final RenderFallingTreeBlocks renderTreeBlocks = new RenderFallingTreeBlocks();
	
	public RenderFallingTree()	{
		this.shadowSize = 0.5F;
	}
    
	public void doRenderFallingTree(EntityFallingTree parEntityFallingTree, double parX, double parY, double parZ, float parYaw, float partialTickTime) {
		World world = parEntityFallingTree.getWorld();
        Block block = Block.blocksList[parEntityFallingTree.blockID];

        if (world.getBlockId(MathHelper.floor_double(parEntityFallingTree.posX), MathHelper.floor_double(parEntityFallingTree.posY), MathHelper.floor_double(parEntityFallingTree.posZ)) != parEntityFallingTree.blockID)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)parX, (float)parY, (float)parZ);
            GL11.glRotatef(parYaw, Direction.offsetZ[parEntityFallingTree.direction],0 , -Direction.offsetX[parEntityFallingTree.direction]);
            this.bindEntityTexture(parEntityFallingTree);
            GL11.glDisable(GL11.GL_LIGHTING);
            Tessellator tessellator;

            if (block != null)
            {
                this.renderTreeBlocks.setRenderBoundsFromBlock(block);
                this.renderTreeBlocks.renderFallingTreeBlock(block, world, MathHelper.floor_double(parEntityFallingTree.posX), MathHelper.floor_double(parEntityFallingTree.posY), MathHelper.floor_double(parEntityFallingTree.posZ), parEntityFallingTree.metadata);
            }
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
		this.doRenderFallingTree((EntityFallingTree)entity, x, y, z, yaw, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TextureMap.locationBlocksTexture;
	}

}
