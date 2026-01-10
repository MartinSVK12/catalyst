package sunsetsatellite.catalyst.multipart;

import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Side;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.block.model.MultipartBlockModelBuilder;
import sunsetsatellite.catalyst.multipart.item.model.ItemModelMultipart;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

public class MultipartModels implements ModelEntrypoint {
	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		ModelHelper.setBlockModel(CatalystMultipart.carpenterWorkbench, () -> {
			return new BlockModelStandard<>(CatalystMultipart.carpenterWorkbench)
				.setTex(0, "catalyst-multipart:block/carpenter_workbench_top", Side.TOP)
				.setTex(0, "catalyst-multipart:block/carpenter_workbench_bottom", Side.BOTTOM)
				.setTex(0, "catalyst-multipart:block/carpenter_workbench_front", Side.NORTH)
				.setTex(0, "catalyst-multipart:block/carpenter_workbench_side", Side.EAST, Side.WEST, Side.SOUTH);
		});

		ModelHelper.setBlockModel(CatalystMultipart.multipartBlock, () -> {
			return new MultipartBlockModelBuilder(CatalystMultipart.MOD_ID).build(CatalystMultipart.multipartBlock);
		});
	}

	@Override
	public void initItemModels(ItemModelDispatcher dispatcher) {
		ModelHelper.setItemModel(CatalystMultipart.multipartItem, () -> {
			return new ItemModelMultipart(CatalystMultipart.multipartItem, CatalystMultipart.MOD_ID);
		});
	}

	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {

	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {

	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {

	}
}
