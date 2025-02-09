package sunsetsatellite.catalyst.fluids.interfaces.mixin;

import sunsetsatellite.catalyst.fluids.impl.MenuFluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

public interface FluidSlotUpdater {
	void catalyst$updateFluidSlot(MenuFluid container, int i, FluidStack fluidStack);
}
