package sunsetsatellite.catalyst.multipart.block.model;

import net.minecraft.core.block.Block;
import sunsetsatellite.catalyst.CatalystMultipart;


public class MultipartBlockModelBuilder {
	private final String modid;
	private boolean render3d = true;
	private float renderScale = 0.25F;
	private ModernMultipartBlockModel modernBlockModel;

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

	public BlockModelMultipart build(Block block) {
		return new BlockModelMultipart(block, this.modernBlockModel, this.render3d, this.renderScale);
	}

	public BlockModelMultipartItem buildItem() {
		return new BlockModelMultipartItem(CatalystMultipart.multipartBlock, this.render3d, this.renderScale);
	}
}
