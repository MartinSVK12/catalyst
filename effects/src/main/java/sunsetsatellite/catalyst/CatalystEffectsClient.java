package sunsetsatellite.catalyst;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.core.net.command.CommandManager;
import sunsetsatellite.catalyst.effects.command.CommandAttributes;
import sunsetsatellite.catalyst.effects.command.CommandEffects;
import sunsetsatellite.catalyst.effects.command.CommandExtraHealth;
import sunsetsatellite.catalyst.effects.interfaces.mixins.IKeybinds;
import turniplabs.halplibe.util.ClientStartEntrypoint;

public class CatalystEffectsClient implements ClientStartEntrypoint {

	public static IKeybinds keybinds;

	@Override
	public void beforeClientStart() {
		CommandManager.registerCommand(new CommandEffects());
		CommandManager.registerCommand(new CommandAttributes());
		CommandManager.registerCommand(new CommandExtraHealth());
	}

	@Override
	public void afterClientStart() {

	}

	public static void addSettingsPage(){
		IKeybinds gameSettings = keybinds = (IKeybinds) Minecraft.getMinecraft().gameSettings;

		CatalystClient.effectsCategory
			.withComponent(new ToggleableOptionComponent<>(gameSettings.getEffectDisplayPlaceEnumOption()))
			.withComponent(new ToggleableOptionComponent<>(gameSettings.getExtraHealthDisplayStyle()));
	}
}
