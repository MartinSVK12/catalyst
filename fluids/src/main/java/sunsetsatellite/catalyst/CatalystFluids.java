package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.core.util.mp.PacketOpenGui;
import sunsetsatellite.catalyst.fluids.mp.PacketFluidWindowClick;
import sunsetsatellite.catalyst.fluids.mp.PacketSetFluidSlot;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.Fluids;
import turniplabs.halplibe.helper.NetworkHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.BlockInitEntrypoint;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.util.ArrayList;
import java.util.List;

public class CatalystFluids implements ModInitializer, GameStartEntrypoint, BlockInitEntrypoint {
	public static final String MOD_ID = "catalyst-fluids";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		NetworkHandler.registerNetworkMessage(PacketFluidWindowClick::new);
		NetworkHandler.registerNetworkMessage(PacketSetFluidSlot::new);
	}

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		LOGGER.info("{} fluid types registered.", Fluid.fluidMap.size());
		LOGGER.info("Catalyst: Fluids initialized.");
	}

	@Override
	public void afterBlockInit() {
		Fluids.init();
	}

	public static ArrayList<FluidStack> condenseFluidList(List<FluidStack> list) {
		ArrayList<FluidStack> stacks = new ArrayList<>();
		for (FluidStack stack : list) {
			if (stack != null) {
				boolean found = false;
				for (FluidStack S : stacks) {
					if (S.isFluidEqual(stack)) {
						S.amount += stack.amount;
						found = true;
					}
				}
				if(!found) stacks.add(stack.copy());
			}
		}
		return stacks;
	}
}
