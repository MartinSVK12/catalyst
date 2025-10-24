package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.util.CommandHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.EffectTags;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRenderer;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRendererDispatcher;
import sunsetsatellite.catalyst.effects.api.effect.render.TintEffectRender;
import sunsetsatellite.catalyst.effects.command.CommandAttributes;
import sunsetsatellite.catalyst.effects.command.CommandEffects;
import sunsetsatellite.catalyst.effects.interfaces.mixins.IKeybinds;
import sunsetsatellite.catalyst.effects.net.SyncEffectContainerForEntityNetworkMessage;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.util.*;

public class CatalystEffects implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "catalyst-effects";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler config;

	static {
		Toml configToml = new Toml("Catalyst: Effects configuration file.");
		config = new TomlConfigHandler(MOD_ID,configToml);
	}
    @Override
    public void onInitialize() {
		NetworkHandler.registerNetworkMessage(SyncEffectContainerForEntityNetworkMessage::new);
		EffectTags.assignTags();
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

		EffectRendererDispatcher.getInstance().addDispatch(
			Effects.DURATION_BOOST,
			new TintEffectRender<>(Effects.DURATION_BOOST, null, 0x30aaff00)
				.setIcon(TextureRegistry.getTexture("minecraft:item/diamond"))
				.setColor(0xFFAAFF00)
		);

		EffectRendererDispatcher.getInstance().addDispatch(
			Effects.EXTRA_HEALTH,
			new EffectRenderer<Effect>(Effects.EXTRA_HEALTH)
			{ @Override public boolean shouldDisplayIcon() { return false; } }
				.setIcon(TextureRegistry.getTexture(Items.FOOD_APPLE.namespaceID))
		);

		Registries.getInstance().register("catalyst:effects",Effects.getInstance());
		Registries.getInstance().register("catalyst:attributes",Attributes.getInstance());
		LOGGER.info(String.format("%d attributes registered.",Attributes.getInstance().size()));
		LOGGER.info(String.format("%d effects registered.",Effects.getInstance().size()));
		LOGGER.info("Catalyst: Effects initialized.");
	}
}
