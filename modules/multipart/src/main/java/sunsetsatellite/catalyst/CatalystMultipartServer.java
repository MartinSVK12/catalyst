package sunsetsatellite.catalyst;

import net.fabricmc.api.DedicatedServerModInitializer;
import sunsetsatellite.catalyst.core.util.mp.GuiEntry;
import sunsetsatellite.catalyst.multipart.menu.MenuCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.tile.TileEntityCarpenterWorkbench;

import static sunsetsatellite.catalyst.CatalystMultipart.key;

public class CatalystMultipartServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		Catalyst.GUIS.register(key("gui/carpenter_workbench"), new GuiEntry<>(TileEntityCarpenterWorkbench.class, MenuCarpenterWorkbench.class));
	}
}
