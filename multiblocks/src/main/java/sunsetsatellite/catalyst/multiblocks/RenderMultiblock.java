package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.core.util.*;

import java.util.ArrayList;
import java.util.Objects;

public class RenderMultiblock extends TileEntityRenderer<TileEntity> {
    @Override
    public void doRender(Tessellator tessellator, TileEntity tileEntity, double d, double e, double f, float g) {
        int i = tileEntity.x;
        int j = tileEntity.y;
        int k = tileEntity.z;
        Direction dir = Direction.getDirectionFromSide(tileEntity.getMovedData()).getOpposite();
        World world = this.renderDispatcher.renderEngine.mc.theWorld;
        if(tileEntity instanceof IMultiblock){
            Multiblock multiblock = ((IMultiblock) tileEntity).getMultiblock();
            ArrayList<BlockInstance> blocks = multiblock.getBlocks(new Vec3i(i, j, k),dir);
            ArrayList<BlockInstance> substitutions = multiblock.getSubstitutions(new Vec3i(i, j, k),dir);
			blockRenderer = new RenderBlocks(new HologramWorld(blocks));
            for (BlockInstance block : blocks) {
                if(!block.exists(world)){
                    boolean foundSub = substitutions.stream().anyMatch((BI)-> BI.pos.equals(block.pos) && BI.exists(world));
                    if(!foundSub){
						if (!Objects.equals(world.getLevelData().getWorldName(), "modelviewer")) {
							GL11.glPushMatrix();
							GL11.glDisable(GL11.GL_LIGHTING);
							GL11.glTranslatef((float)d+(block.pos.x-i)+0.5f, (float)e+(block.pos.y-j)+0.5f, (float)f+(block.pos.z-k)+0.5f);
							BlockModel<?> model = BlockModelDispatcher.getInstance().getDispatch(block.block);
							((IFullbright)model).enableFullbright();
							if(world.getBlockId(block.pos.x,block.pos.y,block.pos.z) != 0){
								((IColorOverride)model).enableColorOverride();
								((IColorOverride)model).overrideColor(1,0,0,0.90f);
								GL11.glScalef(1.1f,1.1f,1.1f);
							} else {
								((IColorOverride)model).overrideColor(1,1,1,0.75f);
								GL11.glScalef(0.75f,0.75f,0.75f);
							}
							drawBlock(tessellator,
								model,
								block.meta == -1 ? 0 : block.meta);
							GL11.glEnable(GL11.GL_LIGHTING);
							GL11.glPopMatrix();
							((IColorOverride)model).overrideColor(1,1,1,1f);
							((IColorOverride)model).disableColorOverride();
							((IFullbright)model).disableFullbright();
						}
					}
                }
            }
        }

    }

	public void drawBlock(Tessellator tessellator, BlockModel<?> model, int meta) {
		TextureRegistry.blockAtlas.bindTexture();
		GL11.glPushMatrix();
		RenderBlocks renderBlocks = BlockModel.renderBlocks;
		BlockModel.setRenderBlocks(blockRenderer);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		model.renderBlockOnInventory(tessellator,meta,1,null);
		BlockModel.setRenderBlocks(renderBlocks);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

    protected RenderBlocks blockRenderer;
}
