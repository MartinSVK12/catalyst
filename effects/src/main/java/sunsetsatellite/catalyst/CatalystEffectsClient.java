package sunsetsatellite.catalyst;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.core.item.Item;
import sunsetsatellite.catalyst.effects.interfaces.mixins.IKeybinds;
import turniplabs.halplibe.util.ClientStartEntrypoint;

public class CatalystEffectsClient implements ClientStartEntrypoint {
	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {
		CatalystEffects.keybinds = ((IKeybinds) Minecraft.getMinecraft(Minecraft.class).gameSettings);
		OptionsPage optionsPage = new OptionsPage("gui.options.page.catalyst-effect", Item.bucketWater.getDefaultStack());
		optionsPage
			.withComponent(new ToggleableOptionComponent<>(CatalystEffects.keybinds.getEffectDisplayPlaceEnumOption()));
		OptionsPages.register(optionsPage);
	}
}
