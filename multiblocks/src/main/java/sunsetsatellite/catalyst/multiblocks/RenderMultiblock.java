package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.RenderEngine;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Vec3i;

import java.util.ArrayList;
import java.util.Objects;

public class RenderMultiblock extends TileEntityRenderer<TileEntity> {
    @Override
    public void doRender(Tessellator tessellator, TileEntity tileEntity, double d, double e, double f, float g) {
        int i = tileEntity.x;
        int j = tileEntity.y;
        int k = tileEntity.z;
        Direction dir = Direction.getDirectionFromSide(tileEntity.getMovedData());
        World world = this.renderDispatcher.renderEngine.mc.theWorld;
        if(tileEntity instanceof IMultiblock){
            Multiblock multiblock = ((IMultiblock) tileEntity).getMultiblock();
            ArrayList<BlockInstance> blocks = multiblock.getBlocks(new Vec3i(i, j, k),Direction.Z_POS); //TODO: multiblocks need to be made in the Z+ direction currently and that's stupid
            ArrayList<BlockInstance> substitutions = multiblock.getSubstitutions(new Vec3i(i, j, k),Direction.Z_POS);
            for (BlockInstance block : blocks) {
                if(!block.exists(world)){
                    boolean foundSub = substitutions.stream().anyMatch((BI)-> BI.pos.equals(block.pos) && BI.exists(world));
                    if(!foundSub){
						if(Objects.equals(world.getLevelData().getWorldName(), "modelviewer")){
							GL11.glPushMatrix();
							GL11.glTranslatef((float)d+(block.pos.x-i), (float)e+(block.pos.y-j), (float)f+(block.pos.z-k));
							drawBlock(tessellator,
								this.renderDispatcher.renderEngine,
								block.block.id,
								block.meta == -1 ? 0 : block.meta,
								i,
								j,
								k,
								tileEntity);
							GL11.glPopMatrix();
						} else {
							GL11.glPushMatrix();
							GL11.glDisable(GL11.GL_LIGHTING);
//                        GL11.glColor4f(1f,0f,0f,1.0f);
							GL11.glTranslatef((float)d+(block.pos.x-i), (float)e+(block.pos.y-j), (float)f+(block.pos.z-k));
							if(world.getBlockId(block.pos.x,block.pos.y,block.pos.z) != 0){
								GL11.glColor4f(1f,0f,0f,1f);
								GL11.glScalef(1.1f,1.1f,1.1f);
								GL11.glTranslatef(-0.05f,0,-0.05f);
							} else {
								GL11.glColor4f(1f,1f,1f,0.5f);
								GL11.glScalef(0.75f,0.75f,0.75f);
								GL11.glTranslatef(0.15f,0.15f,0.15f);
							}
							drawBlock(tessellator,
								this.renderDispatcher.renderEngine,
								block.block.id,
								block.meta == -1 ? 0 : block.meta,
								i,
								j,
								k,
								tileEntity);
							GL11.glEnable(GL11.GL_LIGHTING);
							GL11.glPopMatrix();
						}
                    }
                }
            }
        }

    }

	public void drawBlock(Tessellator tessellator, RenderEngine renderengine, int i, int j, int x, int y, int z, TileEntity tile) {
		renderengine.bindTexture(renderengine.getTexture("/terrain.png"));
		Block block = Block.blocksList[i];
		GL11.glPushMatrix();
		this.blockRenderer.renderStandardBlock(tessellator, BlockModelDispatcher.getInstance().getDispatch(block),block,x,y,z);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

    private final RenderBlocks blockRenderer = new RenderBlocks();
}
