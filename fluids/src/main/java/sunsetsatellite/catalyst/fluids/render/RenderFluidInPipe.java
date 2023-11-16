package sunsetsatellite.catalyst.fluids.render;


import net.minecraft.client.Minecraft;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.RenderEngine;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.CatalystFluids;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityFluidContainer;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityFluidPipe;


public class RenderFluidInPipe extends TileEntityRenderer<TileEntity> {
    @Override
    public void doRender(TileEntity tileEntity1, double d2, double d4, double d6, float f8) {

        int i = tileEntity1.x;
        int j = tileEntity1.y;
        int k = tileEntity1.z;
        World blockAccess = this.renderDispatcher.renderEngine.mc.theWorld;
        Block block = Block.glass;

        GL11.glPushMatrix();

        float fluidAmount = 0;
        float fluidMaxAmount = 1;
        int fluidId = -1;

        if(Minecraft.getMinecraft(Minecraft.class).theWorld.isClientSide){
            if(((TileEntityFluidContainer) tileEntity1).shownFluid != null){
                fluidId = ((TileEntityFluidContainer) tileEntity1).shownFluid.getLiquid().id;
                fluidAmount = ((TileEntityFluidContainer) tileEntity1).shownFluid.amount;
                fluidMaxAmount = ((TileEntityFluidContainer) tileEntity1).shownMaxAmount;
            }
        } else {
            if(((TileEntityFluidContainer) tileEntity1).fluidContents[0] != null){
                if(((TileEntityFluidContainer) tileEntity1).fluidContents[0].getLiquid() != null){
                    fluidMaxAmount = ((TileEntityFluidContainer) tileEntity1).getFluidCapacityForSlot(0);
                    fluidAmount = ((TileEntityFluidContainer) tileEntity1).fluidContents[0].amount;
                    fluidId = ((TileEntityFluidContainer) tileEntity1).fluidContents[0].getLiquid().id;
                }
            }
        }

        boolean flag = blockAccess.getBlockId(i + 1, j, k) == block.id || (blockAccess.getBlockTileEntity(i + 1, j, k) instanceof IFluidInventory);
        boolean flag1 = blockAccess.getBlockId(i - 1, j, k) == block.id || (blockAccess.getBlockTileEntity(i - 1, j, k) instanceof IFluidInventory);
        boolean flag2 = blockAccess.getBlockId(i, j + 1, k) == block.id || (blockAccess.getBlockTileEntity(i, j + 1, k) instanceof IFluidInventory);
        boolean flag3 = blockAccess.getBlockId(i, j - 1, k) == block.id || (blockAccess.getBlockTileEntity(i, j - 1, k) instanceof IFluidInventory);
        boolean flag4 = blockAccess.getBlockId(i, j, k + 1) == block.id || (blockAccess.getBlockTileEntity(i, j, k + 1) instanceof IFluidInventory);
        boolean flag5 = blockAccess.getBlockId(i, j, k - 1) == block.id || (blockAccess.getBlockTileEntity(i, j, k - 1) instanceof IFluidInventory);


        float amount = (fluidAmount / fluidMaxAmount);
        float mapped = (float) CatalystFluids.map(amount,0.0d,1.0d,0.0d,0.5d);

        GL11.glTranslatef((float)d2, (float)d4, (float)d6);
        GL11.glRotatef(0.0f, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.25F, 0.25f , 0.25f);
        if(!(flag2 && flag3)){
            GL11.glScalef(0.5f, mapped,0.5f);
        } else {
            GL11.glScalef(mapped,0.5f,mapped);
        }

        GL11.glDisable(GL11.GL_LIGHTING);

        if(fluidId != -1)
        drawBlock(this.getFontRenderer(), this.renderDispatcher.renderEngine, fluidId, 0,0, 0, 0,tileEntity1);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();

        if(flag){
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d2, (float)d4, (float)d6);
            GL11.glRotatef(0.0f, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.5F, 0.25f , 0.25f);
            GL11.glScalef(0.5f, mapped,0.5f);
            GL11.glDisable(GL11.GL_LIGHTING);
            if(fluidId != -1)
                drawBlock(this.getFontRenderer(), this.renderDispatcher.renderEngine, fluidId, 0,0, 0, 0,tileEntity1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
        if(flag1){
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d2, (float)d4, (float)d6);
            GL11.glRotatef(0.0f, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0f, 0.25f , 0.25f);
            GL11.glScalef(0.5f, mapped,0.5f);
            GL11.glDisable(GL11.GL_LIGHTING);
            if(fluidId != -1)
                drawBlock(this.getFontRenderer(), this.renderDispatcher.renderEngine, fluidId, 0,0, 0, 0,tileEntity1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
        if(flag2 && ((TileEntityFluidPipe) tileEntity1).isPressurized || (blockAccess.getBlockTileEntity(i,j+1,k) instanceof TileEntityFluidPipe && ((TileEntityFluidPipe) blockAccess.getBlockTileEntity(i,j+1,k)).getFluidInSlot(0) != null)){
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d2, (float)d4, (float)d6);
            GL11.glRotatef(0.0f, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.25F, 0.25f , 0.25f);
            GL11.glScalef(mapped, 0.5f,mapped);
            GL11.glDisable(GL11.GL_LIGHTING);
            if(fluidId != -1)
                drawBlock(this.getFontRenderer(), this.renderDispatcher.renderEngine, fluidId, 0,0, 0, 0,tileEntity1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
        if(flag3){
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d2, (float)d4, (float)d6);
            GL11.glRotatef(0.0f, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.25F, -0.25f , 0.25f);
            GL11.glScalef(mapped, 0.5f,mapped);
            GL11.glDisable(GL11.GL_LIGHTING);
            if(fluidId != -1)
                drawBlock(this.getFontRenderer(), this.renderDispatcher.renderEngine, fluidId, 0,0, 0, 0, tileEntity1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
        if(flag4){
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d2, (float)d4, (float)d6);
            GL11.glRotatef(0.0f, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.25F, 0.25f , 0.5f);
            GL11.glScalef(0.5f, mapped,0.5f);
            GL11.glDisable(GL11.GL_LIGHTING);
            if(fluidId != -1)
                drawBlock(this.getFontRenderer(), this.renderDispatcher.renderEngine, fluidId, 0,0, 0, 0, tileEntity1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
        if(flag5){
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d2, (float)d4, (float)d6);
            GL11.glRotatef(0.0f, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.25F, 0.25f , 0.0f);
            GL11.glScalef(0.5f, mapped,0.5f);
            GL11.glDisable(GL11.GL_LIGHTING);
            if(fluidId != -1)
                drawBlock(this.getFontRenderer(), this.renderDispatcher.renderEngine, fluidId, 0,0, 0, 0, tileEntity1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }


    public void drawBlock(FontRenderer fontrenderer, RenderEngine renderengine, int i, int j, int k, int l, int i1, TileEntity tile) {
        renderengine.bindTexture(renderengine.getTexture("/terrain.png"));
        Block f1 = Block.blocksList[i];
        GL11.glPushMatrix();
        this.blockRenderer.renderBlock(f1, j, renderengine.mc.theWorld, tile.x, tile.y, tile.z);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private final RenderFluid blockRenderer = new RenderFluid();
}
