package sunsetsatellite.catalyst;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import sunsetsatellite.catalyst.multipart.interfaces.mixins.IKeybinds;
import turniplabs.halplibe.util.ClientStartEntrypoint;

public class CatalystMultipartClient implements ClientStartEntrypoint {
	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {
		IKeybinds gameSettings = (IKeybinds) Minecraft.getMinecraft(Minecraft.class).gameSettings;
		if(FabricLoader.getInstance().isModLoaded("tmb")){
			CatalystClient.effectsCategory.withComponent(new BooleanOptionComponent(gameSettings.showMultipartsInTMB()));
		}
	}
}
