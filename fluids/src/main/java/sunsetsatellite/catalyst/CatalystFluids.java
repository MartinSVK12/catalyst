package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.Fluids;
import turniplabs.halplibe.util.GameStartEntrypoint;

public class CatalystFluids implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "catalyst-fluids";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

	}

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		Fluids.init();
		LOGGER.info("{} fluid types registered.", Fluid.fluidMap.size());
		LOGGER.info("Catalyst: Fluids initialized.");
	}
}
