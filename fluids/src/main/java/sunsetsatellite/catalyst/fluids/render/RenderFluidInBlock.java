package sunsetsatellite.catalyst.fluids.render;


import net.minecraft.client.Minecraft;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.RenderEngine;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityFluidContainer;


public class RenderFluidInBlock extends TileEntityRenderer<TileEntity> {

    public void drawBlock(Tessellator tessellator, RenderEngine renderengine, int i, TileEntity tile) {
        renderengine.bindTexture(renderengine.getTexture("/terrain.png"));
        Block block = Block.blocksList[i];
        GL11.glPushMatrix();
		this.blockRenderer.renderStandardBlock(tessellator, BlockModelDispatcher.getInstance().getDispatch(block),block,tile.x,tile.y,tile.z);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private final RenderBlocks blockRenderer = new RenderBlocks();

	@Override
	public void doRender(Tessellator tessellator, TileEntity tileEntity, double d, double e, double f, float g) {
		float fluidAmount = 0;
		float fluidMaxAmount = 1;
		int fluidId = 0;//FluidAPI.fluidTank.id;

		if(Minecraft.getMinecraft(Minecraft.class).theWorld.isClientSide){
			if(((TileEntityFluidContainer) tileEntity).shownFluid != null){
				fluidId = ((TileEntityFluidContainer) tileEntity).shownFluid.getLiquid().id;
				fluidAmount = ((TileEntityFluidContainer) tileEntity).shownFluid.amount;
				fluidMaxAmount = ((TileEntityFluidContainer) tileEntity).shownMaxAmount;
			}
		} else {
			if(((TileEntityFluidContainer) tileEntity).fluidContents[0] != null){
				if(((TileEntityFluidContainer) tileEntity).fluidContents[0].getLiquid() != null){
					fluidMaxAmount = ((TileEntityFluidContainer) tileEntity).getFluidCapacityForSlot(0);
					fluidAmount = ((TileEntityFluidContainer) tileEntity).fluidContents[0].amount;
					fluidId = ((TileEntityFluidContainer) tileEntity).fluidContents[0].getLiquid().id;
				}
			}
		}

		float amount = Math.abs((fluidAmount / fluidMaxAmount) - 0.01f);

		if(fluidId != 0){
			GL11.glPushMatrix();
			GL11.glTranslatef((float)d, (float)e, (float)f);
			GL11.glRotatef(0.0f, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(0.99f,amount,0.99f);
			GL11.glTranslatef(0.01F, 0.0f, 0.01f);

			GL11.glDisable(GL11.GL_LIGHTING);
			drawBlock(tessellator, this.renderDispatcher.renderEngine.mc.renderEngine, fluidId, tileEntity);
			GL11.glEnable(GL11.GL_LIGHTING);

			GL11.glPopMatrix();
		}
	}
}
