package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.fluids.mp.packets.PacketFluidWindowClick;
import sunsetsatellite.catalyst.fluids.mp.packets.PacketSetFluidSlot;
import sunsetsatellite.catalyst.fluids.mp.packets.PacketUpdateClientFluidRender;
import sunsetsatellite.catalyst.fluids.registry.FluidRegistry;
import sunsetsatellite.catalyst.fluids.registry.FluidRegistryEntry;
import turniplabs.halplibe.helper.NetworkHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.util.Collections;


public class CatalystFluids implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "catalyst-fluids";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final FluidRegistry FLUIDS = new FluidRegistry();

	static {
		NetworkHelper.register(PacketSetFluidSlot.class, true, true);
		NetworkHelper.register(PacketFluidWindowClick.class, true, true);
		NetworkHelper.register(PacketUpdateClientFluidRender.class, false, true);
	}

    @Override
    public void onInitialize() {
        LOGGER.info("Catalyst: Fluids initialized.");
    }

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
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		FluidRegistryEntry entry = new FluidRegistryEntry("minecraft", Item.bucketWater,Item.bucket, Collections.singletonList((BlockFluid) Block.fluidWaterFlowing));
		FLUIDS.register("minecraft:water",entry);
		entry = new FluidRegistryEntry("minecraft", Item.bucketLava,Item.bucket, Collections.singletonList((BlockFluid) Block.fluidLavaFlowing));
		FLUIDS.register("minecraft:lava",entry);
		Registries.getInstance().register(MOD_ID+":fluids",FLUIDS);
		LOGGER.info("Fluid registry registered.");
		LOGGER.info(FLUIDS.size()+" fluid containers registered.");
	}
}
