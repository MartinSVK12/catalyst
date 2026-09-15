package sunsetsatellite.catalyst;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.option.GameSettings;
import sunsetsatellite.catalyst.multiblocks.Options;

public class CatalystMultiblocksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		GameSettings.register(Options.renderMultiblockPreview);
	}

	public static void addSettingsPage() {
		CatalystClient.multiblocksCategory
			.withComponent(new BooleanOptionComponent(Options.renderMultiblockPreview));
	}
}
