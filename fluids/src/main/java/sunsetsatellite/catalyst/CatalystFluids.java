package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.core.util.mp.PacketOpenGui;
import sunsetsatellite.catalyst.fluids.mp.PacketFluidWindowClick;
import sunsetsatellite.catalyst.fluids.mp.PacketSetFluidSlot;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.Fluids;
import turniplabs.halplibe.helper.NetworkHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;

public class CatalystFluids implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "catalyst-fluids";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		//todo: hardcoding bad
		Packet.addMapping(145,true,true, PacketFluidWindowClick.class);
		Packet.addMapping(146,true,true, PacketSetFluidSlot.class);
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
