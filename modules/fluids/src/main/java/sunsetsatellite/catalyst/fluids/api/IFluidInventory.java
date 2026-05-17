package sunsetsatellite.catalyst.fluids.api;

import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.ArrayList;

public interface IFluidInventory {
	boolean canInsertFluid(int slot, FluidStack fluidStack);

	FluidStack getFluidInSlot(int slot);

	int getFluidCapacityForSlot(int slot);

	ArrayList<Fluid> getAllowedFluidsForSlot(int slot);

	void setFluidInSlot(int slot, FluidStack fluid);

	FluidStack insertFluid(int slot, FluidStack fluidStack);

	int getRemainingCapacity(int slot);

	int getFluidInventorySize();

	void onFluidInventoryChanged();

	int getTransferSpeed();
}
