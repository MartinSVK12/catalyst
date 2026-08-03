package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.fluids.api.IFluidInventory;
import sunsetsatellite.catalyst.fluids.mp.PacketFluidWindowClick;
import sunsetsatellite.catalyst.fluids.mp.PacketSetFluidSlot;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.Fluids;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.dependency.Key;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalystFluids implements ModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("catalyst-fluids", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommonEvents.AFTER_BLOCK_INIT.listen(Key.of(MOD_ID), this::afterBlockInit);
		CommonEvents.AFTER_GAME_START.listen(Key.of(MOD_ID), this::afterGameStart);
		NetworkHandler.registerNetworkMessage(PacketFluidWindowClick::new);
		NetworkHandler.registerNetworkMessage(PacketSetFluidSlot::new);
	}

	public void afterGameStart() {
		LOGGER.info("{} fluid types registered.", Fluid.fluidMap.size());
		LOGGER.info("Catalyst: Fluids initialized.");
	}

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
				if (!found) stacks.add(stack.copy());
			}
		}
		return stacks;
	}

	public static @UnmodifiableView List<FluidStack> collectFluidStacks(IFluidInventory inv) {
		if (inv == null) return Collections.emptyList();
		ArrayList<FluidStack> stacks = new ArrayList<>();

		for (int i = 0; i < inv.getFluidInventorySize(); i++) {
			stacks.add(i, inv.getFluidInSlot(i));
		}

		return Collections.unmodifiableList(stacks);
	}

	public static @UnmodifiableView List<FluidStack> collectAndCondenseFluidStacks(IFluidInventory inv) {
		return condenseFluidList(collectFluidStacks(inv));
	}
}
