package sunsetsatellite.catalyst.multipart.block.model;

import net.minecraft.core.block.Block;
import org.useless.dragonfly.helper.ModelHelper;
import org.useless.dragonfly.model.block.processed.ModernBlockModel;
import org.useless.dragonfly.model.blockstates.data.BlockstateData;
import org.useless.dragonfly.model.blockstates.processed.MetaStateInterpreter;
import sunsetsatellite.catalyst.CatalystMultipart;

public class MultipartBlockModelBuilder {
	private final String modid;
	private boolean render3d = true;
	private float renderScale = 0.25F;
	private ModernBlockModel modernBlockModel;
	private BlockstateData blockstateData;
	private MetaStateInterpreter metaStateInterpreter;

	public MultipartBlockModelBuilder(String modId) {
		this.modid = modId;
	}

	public MultipartBlockModelBuilder setRender3D(boolean render3d) {
		this.render3d = render3d;
		return this;
	}

	public MultipartBlockModelBuilder setRenderScale(float renderScale) {
		this.renderScale = renderScale;
		return this;
	}

	public MultipartBlockModelBuilder setBlockModel(String blockModelPath) {
		this.modernBlockModel = ModelHelper.getOrCreateBlockModel(this.modid, blockModelPath);
		return this;
	}

	public MultipartBlockModelBuilder setBlockModel(String modid, String blockModelPath) {
		this.modernBlockModel = ModelHelper.getOrCreateBlockModel(modid, blockModelPath);
		return this;
	}

	public MultipartBlockModelBuilder setBlockState(String blockStatePath) {
		this.blockstateData = ModelHelper.getOrCreateBlockState(this.modid, blockStatePath);
		return this;
	}

	public MultipartBlockModelBuilder setBlockState(String modid, String blockStatePath) {
		this.blockstateData = ModelHelper.getOrCreateBlockState(modid, blockStatePath);
		return this;
	}

	public MultipartBlockModelBuilder setMetaStateInterpreter(MetaStateInterpreter interpreter) {
		this.metaStateInterpreter = interpreter;
		return this;
	}

	public BlockModelMultipart build(Block block) {
		return new BlockModelMultipart(block, this.modernBlockModel, this.blockstateData, this.metaStateInterpreter, this.render3d, this.renderScale);
	}

	public BlockModelMultipartItem buildItem() {
		return new BlockModelMultipartItem(CatalystMultipart.multipartBlock, this.modernBlockModel, this.blockstateData, this.metaStateInterpreter, this.render3d, this.renderScale);
	}
}
