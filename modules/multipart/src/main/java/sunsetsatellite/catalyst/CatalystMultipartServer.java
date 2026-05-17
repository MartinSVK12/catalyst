package sunsetsatellite.catalyst;

import net.fabricmc.api.DedicatedServerModInitializer;

public class CatalystMultipartServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		//Catalyst.GUIS.register(key("gui/carpenter_workbench"), new MpGuiEntry(TileEntityCarpenterWorkbench.class, MenuCarpenterWorkbench.class));
	}
}
