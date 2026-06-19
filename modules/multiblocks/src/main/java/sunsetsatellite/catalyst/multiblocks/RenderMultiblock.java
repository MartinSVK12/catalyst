package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.LightIndexHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.HologramWorld;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.ArrayList;
import java.util.Objects;

public class RenderMultiblock extends TileEntityRenderer<TileEntity> {
	@Override
	public void doRender(TessellatorGeneral tessellator, TileEntity tileEntity, double d, double e, double f, float g) {
		int i = tileEntity.tilePos.x;
		int j = tileEntity.tilePos.y;
		int k = tileEntity.tilePos.z;
		Direction dir = Direction.getDirectionFromSide(tileEntity.getBlockMeta());
		World world = this.renderDispatcher.textureManager.mc.currentWorld;
		if (tileEntity instanceof IMultiblock) {
			if (((IMultiblock) tileEntity).getMultiblock() == null) {
				return;
			}
			Multiblock multiblock = ((IMultiblock) tileEntity).getMultiblock().data;
			if (((IMultiblock) tileEntity).getMultiblock().isValid()) {
				return;
			}
			ArrayList<BlockInstance> blocks = multiblock.getBlocks(new Vec3i(i, j, k), dir);
			ArrayList<BlockInstance> substitutions = multiblock.getSubstitutions(new Vec3i(i, j, k), dir);
			hologram = new HologramWorld(blocks);
			for (BlockInstance block : blocks) {
				if (!block.exists(world)) {
					boolean foundSub = substitutions.stream().anyMatch((BI) -> BI.pos.equals(block.pos) && BI.exists(world));
					if (!foundSub) {
						if (!Objects.equals(world.getLevelData().getWorldName(), "modelviewer")) {
							GLRenderer.pushFrame();
							Lighting.disable();
							GLRenderer.modelM4f().translate((float) d + (block.pos.x - i) + 0.5f, (float) e + (block.pos.y - j) + 0.5f, (float) f + (block.pos.z - k) + 0.5f);
							BlockModel<?> model = BlockModelDispatcher.getInstance().getDispatch(block.block);
							float alpha = 1;
							if (world.getBlockType(block.pos.tilePos()).id() != 0) {
								GLRenderer.setColor4f(1,0,0,0.90f);
								alpha = 0.9f;
								GLRenderer.modelM4f().scale(1.1f,1.1f,1.1f);
							} else {
								GLRenderer.setColor4f(1,1,1,0.75f);
								alpha = 0.75f;
								GLRenderer.modelM4f().scale(0.9f, 0.9f, 0.9f);
							}
							drawBlock(GLRenderer.getTessellator(),
								model,
								block.meta, alpha);
							Lighting.enableLight();
							GLRenderer.popFrame();
							GLRenderer.setColor4f(1,1,1,1);
						}
					}
				}
			}
		}

	}

	public void drawBlock(TessellatorGeneral t, BlockModel<?> model, int meta, float alpha) {
		TextureRegistry.worldAtlas.bind();
		GLRenderer.pushFrame();
		GLRenderer.setShader(Shaders.WORLD);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
		GLRenderer.setColor4f(1,1,1,alpha);
		model.renderStandalone(t, meta, LightIndexHelper.lightIndex2i(15,15));
		GLRenderer.setColor4f(1,1,1,1);
		GLRenderer.disableState(State.BLEND);
		GLRenderer.popFrame();
		GLRenderer.enableState(State.CULL_FACE);
	}

	protected HologramWorld hologram;
}
