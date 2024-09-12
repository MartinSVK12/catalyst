package sunsetsatellite.catalyst.multipart.block.model;

import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.mixins.mixin.accessor.RenderBlocksAccessor;
import org.useless.dragonfly.model.block.BlockModelDragonFly;
import org.useless.dragonfly.model.block.BlockModelRenderer;
import org.useless.dragonfly.model.block.InternalModel;
import org.useless.dragonfly.model.block.processed.ModernBlockModel;
import org.useless.dragonfly.model.blockstates.data.BlockstateData;
import org.useless.dragonfly.model.blockstates.processed.MetaStateInterpreter;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.api.ISupportsMultiparts;
import sunsetsatellite.catalyst.multipart.api.MultipartType;

import java.util.ArrayList;

public class BlockModelMultipart extends BlockModelDragonFly {

	public BlockModel<?> parentModel;

	public BlockModelMultipart(Block block, ModernBlockModel model, BlockstateData blockstateData, MetaStateInterpreter metaStateInterpreter, boolean render3d, float renderScale) {
		super(block, model, blockstateData, metaStateInterpreter, render3d, renderScale);
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		if(parentModel != null) parentModel.render(tessellator,x,y,z);
		InternalModel[] models = getModelsFromState(block, x, y, z, false);
		boolean didRender = false;
		for (InternalModel model : models) {
			didRender |= BlockModelRenderer.renderModelNormal(tessellator, model.model, block, x, y, z, model.rotationX, -model.rotationY);
		}
		return didRender;
	}

	@Override
	public void renderBlockOnInventory(Tessellator tessellator, int metadata, float brightness, float alpha, @Nullable Integer lightmapCoordinate) {
		if(parentModel != null) parentModel.renderBlockOnInventory(tessellator, metadata, brightness, alpha, lightmapCoordinate);
	}

	@Override
	public IconCoordinate getParticleTexture(Side side, int meta) {
		return super.getParticleTexture(side, meta);
	}

	public InternalModel[] getModelsFromState(Block block, int x, int y, int z, boolean sourceFromWorld){
		RenderBlocksAccessor blocksAccessor = (RenderBlocksAccessor) BlockModelRenderer.getRenderBlocks();
		WorldSource world = blocksAccessor.getBlockAccess();
		TileEntity tile = world.getBlockTileEntity(x,y,z);
		ArrayList<InternalModel> models = new ArrayList<>();
		if(tile instanceof ISupportsMultiparts){
			((ISupportsMultiparts) tile).getParts().forEach((dir,multipart)->{
				if(multipart == null) return;
				//X = 90 -> DOWN
				//X = -90 -> UP
				//Y = 0 || 360 -> NORTH
				//Y = 90 -> EAST
				//Y = 180 -> SOUTH
				//Y = 270 -> WEST
				int rotX = 0;
				int rotY = 0;
				switch (dir) {
					case X_POS:
						rotY = 90;
						break;
					case X_NEG:
						rotY = 270;
						break;
					case Y_POS:
						rotX = -90;
						break;
					case Y_NEG:
						rotX = 90;
						break;
					case Z_POS:
						rotY = 180;
						break;
					case Z_NEG:
						break;
				}
				InternalModel internalModel = new InternalModel(MultipartType.getOrCreateBlockModel(CatalystMultipart.MOD_ID, multipart), rotX, rotY);
				internalModel.model.refreshModel();
				models.add(internalModel);
			});
		}
		return models.toArray(new InternalModel[0]);
	}
}

