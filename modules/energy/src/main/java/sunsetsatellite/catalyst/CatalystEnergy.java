package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;


public class CatalystEnergy implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = HalpLibe.registerMod("catalyst-energy", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler config;

	public static final Tag<Block<?>> ENERGY_CONDUITS_CONNECT = Tag.of("energy_conduits_connect");
	public static final Tag<Block<?>> WIRES_CONNECT = Tag.of("wires_connect");

	static {
		Toml configToml = new Toml("Catalyst: Energy configuration file.");
		configToml.addEntry("energyName", "Energy");
		configToml.addEntry("energySuffix", "E");
		config = new TomlConfigHandler(MOD_ID, configToml);
	}

	public static final String ENERGY_NAME = config.getString("energyName");
	public static final String ENERGY_SUFFIX = config.getString("energySuffix");

	@Override
	public void onInitialize() {

		BlockTags.TAG_LIST.add(ENERGY_CONDUITS_CONNECT);
		BlockTags.TAG_LIST.add(WIRES_CONNECT);

		LOGGER.info("Catalyst: Energy initialized.");
	}

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}
}
