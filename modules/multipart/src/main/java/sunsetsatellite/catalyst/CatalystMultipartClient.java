package sunsetsatellite.catalyst;

import net.fabricmc.api.ClientModInitializer;
import turniplabs.halplibe.util.ClientStartEntrypoint;

import static sunsetsatellite.catalyst.CatalystMultipart.key;

public class CatalystMultipartClient implements ClientModInitializer, ClientStartEntrypoint {
	@Override
	public void onInitializeClient() {
		//Catalyst.GUIS.register(key("gui/carpenter_workbench"), new MpGuiEntryClient(TileEntityCarpenterWorkbench.class, ScreenCarpenterWorkbench.class, MenuCarpenterWorkbench.class));
	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {

	}

	public static void addSettingsPage() {
		/*IKeybinds gameSettings = (IKeybinds) Minecraft.getMinecraft().gameSettings;
		if (FabricLoader.getInstance().isModLoaded("tmb")) {
			CatalystClient.multipartCategory.withComponent(new BooleanOptionComponent(gameSettings.showMultipartsInTMB()));
		}*/
	}


}
