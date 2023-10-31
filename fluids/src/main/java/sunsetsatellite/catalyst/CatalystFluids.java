package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.core.data.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.fluids.registry.FluidRegistry;


public class CatalystFluids implements ModInitializer, PreLaunchEntrypoint {
    public static final String MOD_ID = "catalyst-fluids";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final FluidRegistry FLUIDS = new FluidRegistry();

    @Override
    public void onInitialize() {
		LOGGER.info(FLUIDS.size()+" fluids registered.");
        LOGGER.info("Catalyst: Fluids initialized.");
    }

	@Override
	public void onPreLaunch() {
		Registries.INSTANCE.register(MOD_ID+":fluids",FLUIDS);
		LOGGER.info("Fluid registry registered.");
	}
}
