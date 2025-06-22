package sunsetsatellite.catalyst;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import sunsetsatellite.catalyst.core.util.mp.MpGuiEntryClient;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.interfaces.mixins.IKeybinds;
import sunsetsatellite.catalyst.multipart.menu.MenuCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.screen.ScreenCarpenterWorkbench;
import turniplabs.halplibe.util.ClientStartEntrypoint;

import static sunsetsatellite.catalyst.CatalystMultipart.key;

public class CatalystMultipartClient implements ClientModInitializer, ClientStartEntrypoint {
	@Override
	public void onInitializeClient() {
		Catalyst.GUIS.register(key("gui/carpenter_workbench"),new MpGuiEntryClient(TileEntityCarpenterWorkbench.class, ScreenCarpenterWorkbench.class, MenuCarpenterWorkbench.class));
	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {

	}

	public static void addSettingsPage(){
		IKeybinds gameSettings = (IKeybinds) Minecraft.getMinecraft().gameSettings;
		if(FabricLoader.getInstance().isModLoaded("tmb")){
			CatalystClient.multipartCategory.withComponent(new BooleanOptionComponent(gameSettings.showMultipartsInTMB()));
		}
	}



}
