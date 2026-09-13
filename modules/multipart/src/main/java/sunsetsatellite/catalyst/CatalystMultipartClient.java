package sunsetsatellite.catalyst;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.core.util.helper.Side;
import sunsetsatellite.catalyst.core.util.mp.entry.TileGuiEntry;
import sunsetsatellite.catalyst.multipart.block.model.BlockModelMultipart;
import sunsetsatellite.catalyst.multipart.item.model.ItemModelMultipart;
import sunsetsatellite.catalyst.multipart.menu.MenuCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.screen.ScreenCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.tile.TileEntityCarpenterWorkbench;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

import static sunsetsatellite.catalyst.CatalystMultipart.MOD_ID;
import static sunsetsatellite.catalyst.CatalystMultipart.key;

public class CatalystMultipartClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientEvents.BLOCK_MODEL_RELOAD.listen(Key.of(MOD_ID), this::blockModelReload);
		ClientEvents.ITEM_MODEL_RELOAD.listen(Key.of(MOD_ID), this::itemModelReload);
		Catalyst.GUIS.register(key("gui/carpenter_workbench"), new TileGuiEntry<>(TileEntityCarpenterWorkbench.class,MenuCarpenterWorkbench.class, ScreenCarpenterWorkbench::new));
	}

	private void blockModelReload(BlockModelDispatcher dispatcher) {
		dispatcher.addDispatch(CatalystMultipart.multipartBlock, new BlockModelMultipart(CatalystMultipart.multipartBlock));
		dispatcher.addDispatch(CatalystMultipart.carpenterWorkbench,
			new BlockModelStandard<>(CatalystMultipart.carpenterWorkbench)
				.setTex("catalyst-multipart:block/carpenter_workbench_top", Side.TOP)
				.setTex("catalyst-multipart:block/carpenter_workbench_bottom", Side.BOTTOM)
				.setTex("catalyst-multipart:block/carpenter_workbench_front", Side.NORTH)
				.setTex("catalyst-multipart:block/carpenter_workbench_side", Side.EAST, Side.WEST, Side.SOUTH)
		);

	}

	private void itemModelReload(ItemModelDispatcher dispatcher) {
		dispatcher.addDispatch(CatalystMultipart.multipartItem, new ItemModelMultipart(CatalystMultipart.multipartItem));
	}

	public static void addSettingsPage() {
		/*IKeybinds gameSettings = (IKeybinds) Minecraft.getMinecraft().gameSettings;
		if (FabricLoader.getInstance().isModLoaded("tmb")) {
			CatalystClient.multipartCategory.withComponent(new BooleanOptionComponent(gameSettings.showMultipartsInTMB()));
		}*/
	}


}
