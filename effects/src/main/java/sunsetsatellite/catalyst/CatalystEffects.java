package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.data.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.command.AttributesCommand;
import sunsetsatellite.catalyst.effects.command.EffectsCommand;
import sunsetsatellite.catalyst.effects.interfaces.mixins.IKeybinds;
import turniplabs.halplibe.helper.CommandHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.util.*;

public class CatalystEffects implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "catalyst-effects";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler config;
	public static IKeybinds keybinds;

	static {
		Toml configToml = new Toml("Catalyst: Effects configuration file.");
		config = new TomlConfigHandler(MOD_ID,configToml);
	}
    @Override
    public void onInitialize() {
		CommandHelper.createCommand(new EffectsCommand("effect"));
		CommandHelper.createCommand(new AttributesCommand("attribute"));
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		Registries.getInstance().register("catalyst:effects",Effects.getInstance());
		Registries.getInstance().register("catalyst:attributes",Attributes.getInstance());
		LOGGER.info(String.format("%d attributes registered.",Attributes.getInstance().size()));
		LOGGER.info(String.format("%d effects registered.",Effects.getInstance().size()));
		LOGGER.info("Catalyst: Effects initialized.");
	}

	public static <K,V> Map<K,V> mapOf(K[] keys, V[] values){
		if(keys.length != values.length){
			throw new IllegalArgumentException("Arrays differ in size!");
		}
		HashMap<K,V> map = new HashMap<>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i],values[i]);
		}
		return map;
	}

	@SafeVarargs
	public static <T> List<T> listOf(T... values){
		return new ArrayList<>(Arrays.asList(values));
	}

}
