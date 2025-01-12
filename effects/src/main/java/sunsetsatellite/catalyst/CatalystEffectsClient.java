package sunsetsatellite.catalyst;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import sunsetsatellite.catalyst.effects.interfaces.mixins.IKeybinds;
import turniplabs.halplibe.util.ClientStartEntrypoint;

public class CatalystEffectsClient implements ClientStartEntrypoint {
	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {
		CatalystEffects.keybinds = ((IKeybinds) Minecraft.getMinecraft(Minecraft.class).gameSettings);
		CatalystClient.effectsCategory.withComponent(new ToggleableOptionComponent<>(CatalystEffects.keybinds.getEffectDisplayPlaceEnumOption()));
	}
}
