package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.util.CommandHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.command.CommandAttributes;
import sunsetsatellite.catalyst.effects.command.CommandEffects;
import sunsetsatellite.catalyst.effects.interfaces.mixins.IKeybinds;
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

    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		CommandManager.registerCommand(new CommandEffects());
		CommandManager.registerCommand(new CommandAttributes());
		Registries.getInstance().register("catalyst:effects",Effects.getInstance());
		Registries.getInstance().register("catalyst:attributes",Attributes.getInstance());
		LOGGER.info(String.format("%d attributes registered.",Attributes.getInstance().size()));
		LOGGER.info(String.format("%d effects registered.",Effects.getInstance().size()));
		LOGGER.info("Catalyst: Effects initialized.");
	}
}
