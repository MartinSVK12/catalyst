package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;


public class CatalystEnergy implements ModInitializer {
    public static final String MOD_ID = "catalyst-energy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler config;

	static {
		Toml configToml = new Toml("Catalyst: Energy configuration file.");
		configToml.addEntry("energyName","Energy");
		configToml.addEntry("energySuffix","E");
		config = new TomlConfigHandler(MOD_ID,configToml);
	}
	public static final String ENERGY_NAME = config.getString("energyName");
	public static final String ENERGY_SUFFIX = config.getString("energySuffix");

	public static double map(double valueCoord1,
							 double startCoord1, double endCoord1,
							 double startCoord2, double endCoord2) {

		final double EPSILON = 1e-12;
		if (Math.abs(endCoord1 - startCoord1) < EPSILON) {
			throw new ArithmeticException("Division by 0");
		}

		double ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
		return ratio * (valueCoord1 - startCoord1) + startCoord2;
	}
    @Override
    public void onInitialize() {
        LOGGER.info("Catalyst: Energy initialized.");
    }
}
