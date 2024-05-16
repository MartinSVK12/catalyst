package sunsetsatellite.catalyst.fluids.render;


import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.RenderEngine;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityMultiFluidTank;
import sunsetsatellite.catalyst.fluids.util.FluidStack;


public class RenderMultiFluidInBlock extends TileEntityRenderer<TileEntity> {
    @Override
    public void doRender(Tessellator tessellator, TileEntity tileEntity, double d2, double d4, double d6, float f8) {
        TileEntityMultiFluidTank tile = (TileEntityMultiFluidTank) tileEntity;
        float fluidAmount = 0;
        float fluidMaxAmount = 1;
        int fluidId = 0;

        float i = 0;
        for (FluidStack fluidStack : tile.fluidContents) {
            fluidMaxAmount = tile.fluidCapacity;
            fluidAmount = fluidStack.amount;
            fluidId = fluidStack.getLiquid().id;
            float amount = Math.abs((fluidAmount / fluidMaxAmount) - 0.01f);
            if(fluidId != 0){
                GL11.glPushMatrix();
                GL11.glTranslatef((float)d2, (float)d4+i, (float)d6);
                GL11.glRotatef(0.0f, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(0.99f,amount,0.99f);
                GL11.glTranslatef(0.01F, 0.0f, 0.01f);
                i+=1*amount;
                GL11.glDisable(GL11.GL_LIGHTING);
                drawBlock(tessellator, this.renderDispatcher.renderEngine.mc.renderEngine, fluidId, tileEntity);
                GL11.glEnable(GL11.GL_LIGHTING);

                GL11.glPopMatrix();
            }
        }

    }


	public void drawBlock(Tessellator tessellator, RenderEngine renderengine, int i, TileEntity tile) {
		renderengine.bindTexture(renderengine.getTexture("/terrain.png"));
		Block block = Block.blocksList[i];
		GL11.glPushMatrix();
		this.blockRenderer.renderStandardBlock(tessellator, BlockModelDispatcher.getInstance().getDispatch(block),block,tile.x,tile.y,tile.z);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

    private final RenderBlocks blockRenderer = new RenderBlocks();
}
