package sunsetsatellite.catalyst;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.command.CommandManager;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRenderer;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRendererDispatcher;
import sunsetsatellite.catalyst.effects.api.effect.render.TintEffectRender;
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
		EffectRendererDispatcher.getInstance().addDispatch(
			Effects.DURATION_BOOST,
			new TintEffectRender<>(Effects.DURATION_BOOST, null, 0x30aaff00)
				.setIcon(TextureRegistry.getTexture("minecraft:item/diamond"))
				.setColor(0xFFAAFF00)
		);

		EffectRendererDispatcher.getInstance().addDispatch(
			Effects.EXTRA_HEALTH,
			new EffectRenderer<Effect>(Effects.EXTRA_HEALTH) {
				@Override
				public boolean shouldDisplayIcon() {
					return false;
				}
			}
				.setIcon(TextureRegistry.getTexture(Items.FOOD_APPLE.namespaceID))
		);
	}

	public static void addSettingsPage() {
		IKeybinds gameSettings = keybinds = (IKeybinds) Minecraft.getMinecraft().gameSettings;

		CatalystClient.effectsCategory
			.withComponent(new ToggleableOptionComponent<>(gameSettings.getEffectDisplayPlaceEnumOption()))
			.withComponent(new BooleanOptionComponent(gameSettings.getRenderAttributeIcon()))
			.withComponent(new ToggleableOptionComponent<>(gameSettings.getExtraHealthDisplayStyle()));
	}
}
