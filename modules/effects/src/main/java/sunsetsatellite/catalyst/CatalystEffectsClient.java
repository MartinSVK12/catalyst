package sunsetsatellite.catalyst;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.command.CommandManager;
import sunsetsatellite.catalyst.effects.Options;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRenderer;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRendererDispatcher;
import sunsetsatellite.catalyst.effects.api.effect.render.TintEffectRender;
import sunsetsatellite.catalyst.effects.command.CommandAttributes;
import sunsetsatellite.catalyst.effects.command.CommandEffects;
import sunsetsatellite.catalyst.effects.command.CommandExtraHealth;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

@Environment(EnvType.CLIENT)
public class CatalystEffectsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		GameSettings.register(Options.effectDisplayPlaceEnumOption);
		GameSettings.register(Options.effectExtraHealthDisplayStyleEnumOption);
		GameSettings.register(Options.renderAttributeIcon);

		ClientEvents.BEFORE_CLIENT_START.listen(Key.of(Catalyst.MOD_ID),this::beforeClientStart);
		ClientEvents.AFTER_CLIENT_START.listen(Key.of(Catalyst.MOD_ID),this::afterClientStart);
	}

	public void beforeClientStart() {
		CommandManager.registerCommand(new CommandEffects());
		CommandManager.registerCommand(new CommandAttributes());
		CommandManager.registerCommand(new CommandExtraHealth());
	}

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
		CatalystClient.effectsCategory
			.withComponent(new ToggleableOptionComponent<>(Options.effectExtraHealthDisplayStyleEnumOption))
			.withComponent(new BooleanOptionComponent(Options.renderAttributeIcon));
	}
}
