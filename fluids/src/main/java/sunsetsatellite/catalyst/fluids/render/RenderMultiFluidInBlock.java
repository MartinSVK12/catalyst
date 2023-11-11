package sunsetsatellite.catalyst.fluids.render;


import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.RenderEngine;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityMultiFluidTank;
import sunsetsatellite.catalyst.fluids.util.FluidStack;


public class RenderMultiFluidInBlock extends TileEntityRenderer<TileEntity> {
    @Override
    public void doRender(TileEntity tileEntity, double d2, double d4, double d6, float f8) {
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
                drawBlock(this.getFontRenderer(), this.renderDispatcher.renderEngine.minecraft.renderEngine, fluidId, 0,0, 0, 0, tileEntity);
                GL11.glEnable(GL11.GL_LIGHTING);

                GL11.glPopMatrix();
            }
        }

    }


    public void drawBlock(FontRenderer fontrenderer, RenderEngine renderengine, int i, int j, int k, int l, int i1, TileEntity tile) {
        renderengine.bindTexture(renderengine.getTexture("/terrain.png"));
        Block f1 = Block.blocksList[i];
        GL11.glPushMatrix();
        this.blockRenderer.renderBlock(f1, j, renderengine.minecraft.theWorld, tile.xCoord, tile.yCoord, tile.zCoord);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private final RenderFluid blockRenderer = new RenderFluid();
}
