package sunsetsatellite.catalyst;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import sunsetsatellite.catalyst.effects.interfaces.mixins.IKeybinds;
import turniplabs.halplibe.util.ClientStartEntrypoint;

public class CatalystEffectsClient implements ClientStartEntrypoint {

	public static IKeybinds keybinds;

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {

	}

	public static void addSettingsPage(){
		IKeybinds gameSettings = keybinds = (IKeybinds) Minecraft.getMinecraft().gameSettings;
		CatalystClient.effectsCategory.withComponent(new ToggleableOptionComponent<>(gameSettings.getEffectDisplayPlaceEnumOption()));
	}
}
