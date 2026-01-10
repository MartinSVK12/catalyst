package sunsetsatellite.catalyst;

import net.fabricmc.api.DedicatedServerModInitializer;
import sunsetsatellite.catalyst.core.util.mp.MpGuiEntry;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.menu.MenuCarpenterWorkbench;

import static sunsetsatellite.catalyst.CatalystMultipart.key;

public class CatalystMultipartServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		Catalyst.GUIS.register(key("gui/carpenter_workbench"), new MpGuiEntry(TileEntityCarpenterWorkbench.class, MenuCarpenterWorkbench.class));
	}
}
