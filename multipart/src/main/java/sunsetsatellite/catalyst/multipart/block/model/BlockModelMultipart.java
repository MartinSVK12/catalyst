package sunsetsatellite.catalyst.multipart.block.model;

import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.Nullable;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.api.ISupportsMultiparts;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.helper.ModelHelper;
import sunsetsatellite.catalyst.multipart.mixin.accessor.RenderBlocksAccessor;
import sunsetsatellite.catalyst.multipart.util.MultipartModelRenderer;

import java.util.ArrayList;

public class BlockModelMultipart extends BlockModelStandard<Block> {

	public BlockModel<?> parentModel;
	public ModernMultipartBlockModel baseModel;
	public boolean render3d;
	public float renderScale;

	public BlockModelMultipart(Block block, ModernMultipartBlockModel model, boolean render3d, float renderScale) {
		super(block);
		this.baseModel = model;
		this.render3d = render3d;
		this.renderScale = renderScale;
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		if(parentModel != null) parentModel.render(tessellator,x,y,z);
		MultipartInternalModel[] models = getModelsFromState(block, x, y, z, false);
		boolean didRender = false;
		for (MultipartInternalModel model : models) {
			//X = 90 -> DOWN | Y NEG
			//X = -90 -> UP | Y POS
			//Y = 0 || 360 -> NORTH | Z NEG
			//Y = 90 -> EAST | X POS
			//Y = 180 -> SOUTH | Z POS
			//Y = 270 -> WEST | X NEG
			if(model instanceof MultipartInternalModel){
				didRender |= MultipartModelRenderer.renderModelNormal(tessellator, model.model, block, x, y, z, ((MultipartInternalModel) model).side, ((MultipartInternalModel) model).part);
			}
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

	public MultipartInternalModel[] getModelsFromState(Block block, int x, int y, int z, boolean sourceFromWorld){
		RenderBlocksAccessor blocksAccessor = (RenderBlocksAccessor) MultipartModelRenderer.getRenderBlocks();
		WorldSource world = blocksAccessor.getBlockAccess();
		TileEntity tile = world.getBlockTileEntity(x,y,z);
		ArrayList<MultipartInternalModel> models = new ArrayList<>();
		if(tile instanceof ISupportsMultiparts){
			((ISupportsMultiparts) tile).getParts().forEach((dir,multipart)->{
				if(multipart == null) return;
				//X = 90 -> DOWN | Y NEG
				//X = -90 -> UP | Y POS
				//Y = 0 || 360 -> NORTH | Z NEG
				//Y = 90 -> EAST | X POS
				//Y = 180 -> SOUTH | Z POS
				//Y = 270 -> WEST | X NEG
				MultipartInternalModel internalModel = new MultipartInternalModel(ModelHelper.getOrCreateBlockModel(CatalystMultipart.MOD_ID, multipart), dir.getSide(), multipart);
				internalModel.model.refreshModel();
				models.add(internalModel);
			});
		}
		return models.toArray(new MultipartInternalModel[0]);
	}

	@Override
	public boolean shouldItemRender3d() {
		return render3d;
	}

	@Override
	public float getItemRenderScale() {
		return renderScale;
	}
}

